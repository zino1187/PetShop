package com.pet.controller.shopping;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.pet.controller.common.Pager;
import com.pet.domain.Cart;
import com.pet.domain.Member;
import com.pet.domain.OrderSummary;
import com.pet.domain.Receiver;
import com.pet.model.product.ProductService;
import com.pet.model.shopping.ShoppingService;

@Controller
public class ShoppingController {
	@Autowired
	private ShoppingService shoppingService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private Pager pager;
	
	//장바구니 담기!! (세션 체크하기)
	@RequestMapping(value="/shop/cart/regist",method=RequestMethod.POST)
	public String regist(Model model, Cart cart, HttpServletRequest request) {
		
		String view=null;
		HttpSession session = request.getSession();
		
		List<Cart> cartList =(List)session.getAttribute("cartList");
		
		if(cartList==null) {//장바구니에 담을 리스트가 최초라면..
			cartList = new ArrayList<Cart>();
			session.setAttribute("cartList", cartList);
		}
		
		int count=0;//존재여부를 체크하기 위한 카운터 변수(존재할 경우 0보다 큼)
		Cart obj=null;
		
		//장바구니에 등록된 List내에 존재하는 제품인지 체크
		for(int i=0;i<cartList.size();i++) {
			obj = cartList.get(i);
			//이미 존재하면
			if(obj.getProduct_id()==cart.getProduct_id()) {
				count++;
				obj.setEa(obj.getEa()+1);
			}
		}
		if(count==0) {//이미 존재함
			cart.setEa(1);//상세보기에서 장바구니에 담을때는 1개가 된다!!
			cartList.add(cart);//장바구니 리스트에 상품 추가!!
		}
		model.addAttribute("msg", "장바구니에 "+cart.getProduct_name()+" 담겼습니다");
		model.addAttribute("url", "/shop/cart/list");
		
		view= "view/message";

		return view;
	}
	
	//장바구니 목록 가져오기!!
	@RequestMapping(value="/shop/cart/list",method=RequestMethod.GET)
	public String getList(Model model, HttpServletRequest request) {
		//로그인 하지 않은 회원인경우, 거부처리!!
		String view=null;
		view="redirect:/shop/cart.jsp";
		return view;
	}
	
	//장바구니에서 상품 1개 삭제!
	@RequestMapping(value="/shop/cart/del",method=RequestMethod.GET)
	public String removeOne(Model model,@RequestParam int product_id, HttpSession session) {
		//cartList 에서 product_id 를 조회하여, 해당 객체를 List 에서
		//제거한다
		List<Cart> cartList = (List)session.getAttribute("cartList");
		
		for(int i=0;i<cartList.size();i++) {
			Cart cart=cartList.get(i);
			if(product_id==cart.getProduct_id()) {//발견되면
				cartList.remove(cart);//리스트에서 상품 삭제!!
			}
		}
		model.addAttribute("msg", "삭제완료");
		model.addAttribute("url", "/shop/cart/list");
		
		return "view/message";
	}
	
	//장바구니에서 상품 1개 삭제!
	@RequestMapping(value="/shop/cart/remove",method=RequestMethod.GET)
	public String removeAll(Model model,HttpSession session){
		session.removeAttribute("cartList");
		
		model.addAttribute("msg", "장바구니를 비웠습니다");
		model.addAttribute("url", "/shop/cart/list");
		
		return "view/message";
	}
	
	@RequestMapping(value="/shop/cart/edit",method=RequestMethod.GET)
	public String edit(Model model,Cart cart, HttpSession session){
		List<Cart> cartList=(List)session.getAttribute("cartList");
		
		//List에 들어잇는 Cart 객체 끄집어 내기 
		for(int i=0;i<cartList.size();i++) {
			Cart obj=cartList.get(i);
			if(obj.getProduct_id() == cart.getProduct_id()) {
				obj.setEa(cart.getEa());//갯수 대체!!
			}
		}
		model.addAttribute("msg", "장바구니가 수정되었습니다");
		model.addAttribute("url", "/shop/cart/list");
		
		return "view/message";
	}
	
	//구매 1단계 화면 보기 (고객정보,결제정보 등 입력 페이지)
	@RequestMapping(value="/shop/step1",method=RequestMethod.GET)
	public String goStep1(HttpSession session, HttpServletRequest request) {
		//만일 db관련 작업이 잇다면 여기서 처리...
		//클라이언트가 어떤 페이지에서 요청을 시도했는지 , 이전 페이지 정보
		String referer = request.getHeader("referer");
		/*
		 URL : http://localhost:7777/shop/detail
		 URI : /shop/detail
	   */
		try {
			URI uri=new URI(referer); //java.net.URI
			
			//바로구매가 아니라면
			if(uri.getPath().equals("/shop/detail")==false) {
				session.removeAttribute("cartOne");//바로구매 정보 삭제
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return "shop/step1";
	}
	
	//구매 2단계 화면 보기 (결제정보 확인 )
	@RequestMapping(value="/shop/step2",method=RequestMethod.POST)
	public String goStep2(Model model, HttpServletRequest request, OrderSummary orderSummary) {
		HttpSession session  =request.getSession();
		Member member = (Member)session.getAttribute("member");
		//주문상품 정보 (장바구니에 들어있음) 세션이 있으므로, 가져갈 필요X
		//고객정보 (Member 에 들어있음) 세션에 있으므로, 가져갈 필요 X
		//받는자 정보 (파라미터에 들어있음) 저장 후 페이지에서 출력 
		Receiver receiver = orderSummary.getReceiver();
		System.out.println(receiver.getRname());
		System.out.println(receiver.getRphone());
		System.out.println(receiver.getRaddr());
		System.out.println("session"+session);
		
		//jsp에서 보여질 정보 저장
		model.addAttribute("orderSummary", orderSummary);
		
		return "shop/step2";
	}
	
	//구매 3단계 요청 처리 (결제정보 입력 )
	@RequestMapping(value="/shop/step3",method=RequestMethod.POST)
	public String goStep3(Model model, HttpServletRequest request, OrderSummary orderSummary) {
		HttpSession session=request.getSession();
		//주문요약 정보 중 누가 샀는지를 결정!!
		Member member = (Member)session.getAttribute("member");
		orderSummary.setMember(member);
	
		//서비스에게 일시키기 
		List cartList =(List)session.getAttribute("cartList");
		
		System.out.println("cartOne is "+session.getAttribute("cartOne"));
		
		if(session.getAttribute("cartOne")!=null) {
			cartList = (List)session.getAttribute("cartOne");
		}
		shoppingService.insert(cartList ,orderSummary);
		
		//장바구니 모두 비우기!!
		session.removeAttribute("cartList");//장바구니 내역
		session.removeAttribute("cartOne");//바로구매 내역
		
		//내일은 주문 상세도 service에서 처리할 것임!!
		
		
		model.addAttribute("msg", "받을사람 정보는 "+orderSummary.getReceiver().getReceiver_id());
		model.addAttribute("url", "/");
		
		return "view/message";
	}
	
	//바로구매 요청 처리 (장바구니에 1건의 상품을 담는 처리)
	@RequestMapping(value="/shop/buy",method=RequestMethod.POST)
	public String buy(Model model, Cart cart, HttpServletRequest request) {
		
		String view=null;
		HttpSession session = request.getSession();
		if(session.getAttribute("member")==null) {
			model.addAttribute("msg", "로그인이 필요한 서비스입니다");
			view="view/error";
		}else{
			List<Cart> cartOne =(List)session.getAttribute("cartOne");
			
			if(cartOne==null) {//장바구니에 담을 리스트가 최초라면..
				cartOne = new ArrayList<Cart>();
				cartOne.add(cart);//장바구니에 한건 추가!!
			}else {//이미 있다면
				cartOne.removeAll(cartOne);
			}
			session.setAttribute("cartOne", cartOne);
			view= "redirect:/shop/step1";
		};
		return view;
	}
	
	
	//쇼핑 상품 목록
	@RequestMapping(value="/product/list",method=RequestMethod.GET)
	public String getProductList(HttpServletRequest request,Model model) {
		List productList = productService.selectAll();
		
		model.addAttribute("productList", productList);
		model.addAttribute("pager", pager);//이거 넣기!!!

		pager.init(productList, request);
		
		return "shop/list";
	}
}


















