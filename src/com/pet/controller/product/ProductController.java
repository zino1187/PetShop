package com.pet.controller.product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.pet.controller.common.Pager;
import com.pet.domain.Event;
import com.pet.domain.EventProduct;
import com.pet.domain.Product;
import com.pet.exception.DMLException;
import com.pet.exception.FileException;
import com.pet.model.common.file.FileManager;
import com.pet.model.product.ProductService;

@Controller
public class ProductController {
	/*DI방식도 100% 의존성을 탈피하지는 못한 것*/
	@Autowired
	private ProductService productService;
	
	@Autowired
	private Pager pager;
	
	@RequestMapping(value="/admin/product/regist", method=RequestMethod.POST)
	public ModelAndView regist(Product product, HttpServletRequest request) {
		//파일업로드 처리~~~
		System.out.println("카테고리 id "+product.getCategory().getCategory_id());
		System.out.println("상품명은 "+product.getProduct_name());
		System.out.println("가격은 "+product.getPrice());
		System.out.println("브랜드 "+product.getBrand());
		
		//Product VO가 보유한 MultipartFile 안에 업로드된 파일 정보가
		//들어있다..따라서 메모리상에서 존재하므로, 원하는 경로에 저장하자!!
		//FileManager.saveFile(product.getMyFile(), request.getServletContext().getRealPath("/data/"));
		productService.regist(product, 
				product.getMyFile(), 
				request.getServletContext().getRealPath("/data/"));	
		
		ModelAndView mav = new ModelAndView();
		mav.addObject("url", "/admin/product/list");
		mav.addObject("msg", "파일업로드 성공");
		mav.setViewName("view/message");
		
		return mav;
	}
	
	@RequestMapping(value="/admin/product/list", method=RequestMethod.GET)
	public ModelAndView selectAll(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		
		List productList = productService.selectAllJoin();
		
		List eventList = productService.getEvenetList();
		
		//페이징 처리 객체 
		pager.init(productList, request);
		mav.addObject("productList", productList);
		mav.addObject("eventList", eventList);
		mav.addObject("pager", pager);
		
		mav.setViewName("admin/product/index");
		return mav;
	}
	
	//상세보기 요청 
	@RequestMapping(value="/admin/product/detail", method=RequestMethod.GET)
	public String select(Model model,@RequestParam int product_id) {
		System.out.println("product_id : "+product_id);
		Product product=productService.select(product_id);
		model.addAttribute("product", product);
		return "admin/product/detail";
	}
	
	//상세보기 요청 
	@RequestMapping(value="/admin/product/delete", method= {RequestMethod.GET, RequestMethod.POST})
	public String delete(HttpServletRequest request, Model model,@RequestParam int product_id,@RequestParam String filename) {
		//파일도 삭제!!
		String realPath=request.getServletContext().getRealPath("/data/"+filename);
		
		FileManager.removeFile(realPath);
		productService.delete(product_id);
		
		model.addAttribute("url", "/admin/product/list");
		model.addAttribute("msg", "삭제 성공");
		return "view/message";
	}
	
	
	//수정 요청 처리 
	@RequestMapping(value="/admin/product/edit", method= RequestMethod.POST)
	public String update(Model model,Product product, HttpServletRequest request) {
		productService.update(product, request.getServletContext().getRealPath("/data/"));
		
		model.addAttribute("msg", "수정 성공");
		model.addAttribute("url", "/admin/product/detail?product_id="+product.getProduct_id());
		return "view/message";

	}
	
	//사용자 요청 처리 
	//상세보기 요청 
	@RequestMapping(value="/shop/detail", method=RequestMethod.GET)
	public String getDetail(Model model,@RequestParam int product_id) {
		Product product=productService.select(product_id);
		model.addAttribute("product", product);
		return "shop/detail";
	}

	/* 기획상품 관련 */
	@RequestMapping(value="/event/list", method=RequestMethod.GET)
	public String getEventList(HttpServletRequest request, Model model) {
		//어떤 기획이벤트가 있는 목록 가져오자
		List<Event> eventList = productService.getEvenetList();
		
		Map<String, List> listMap = new HashMap<String, List>();
		Map<String, String> titleMap = new HashMap<String, String>();
		
		for(int i=0;i<eventList.size();i++) {
			Event event=eventList.get(i);
			System.out.println(event.getTitle()+":"+event.getEvent_id());
			
			//현재 이벤트에 등록된 상품목록 가져오기!!
			List eventProductList=productService.selectJoinByEventId(event.getEvent_id());
			listMap.put("eventProductList"+i, eventProductList);
			titleMap.put("title"+i, event.getTitle());
		}
		model.addAttribute("listMap", listMap);
		model.addAttribute("titleMap", titleMap);
		
		return "event/list";
	}
	
	/* 기획상품 관리 */
	@RequestMapping(value="/admin/event/list", method=RequestMethod.GET)
	public String getEventListAdmin() {
		
		return "admin/event/list";
	}
	
	@RequestMapping(value="/admin/event/regist", method=RequestMethod.POST)
	@ResponseBody
	public String registEvent(Event event) {
		String result=null;
		try {
			productService.registEvent(event);
			result="ok";
		} catch (DMLException e) {
			e.printStackTrace();
			result="fail";
		}
		return result;
	}

	@RequestMapping(value="/admin/event/selectall",method=RequestMethod.GET,produces="text/html;charset=utf8")
	@ResponseBody
	public String selectAll() {
		List<Event> eventList=productService.getEvenetList();
		
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"eventList\":[");
		for(int i=0;i<eventList.size();i++) {
			Event event = eventList.get(i);
			sb.append("{");
			sb.append("\"event_id\":"+event.getEvent_id()+",");
			sb.append("\"title\":\""+event.getTitle()+"\"");
			if(i<eventList.size()-1) {
				sb.append("},");
			}else {
				sb.append("}");
			}
		}
		sb.append("]");
		sb.append("}");
		
		return sb.toString();
	}	
	
	//기존 상품을 기획상품으로 등록요청 처리 
	@RequestMapping(value="admin/eventproduct/regist", method=RequestMethod.POST)
	public String registEventProduct(EventProduct eventProduct, int[] product_id) {
		
		for(int i=0;i<product_id.length;i++) {
			
			Product product = new Product();
			product.setProduct_id(product_id[i]);
			eventProduct.setProduct(product);
			productService.registEventProduct(eventProduct);
		}
		return "admin/product/index";
	}
	
	
	@ExceptionHandler({FileException.class, DMLException.class})
	public ModelAndView handle(FileException e, DMLException e2) {
		ModelAndView mav = new ModelAndView();
		
		//파일 업로드 에러인 경우
		if(e !=null) {
			mav.addObject("e", e);//에러 객체 자체를 담는다!!
			mav.addObject("msg", e.getMessage());//에러 객체 자체를 담는다!!
		}else if(e2 !=null) {
			mav.addObject("e", e2);//에러 객체 자체를 담는다!!
			mav.addObject("msg", e2.getMessage());//에러 객체 자체를 담는다!!
		}
		//입력 에러엔 경우 
		mav.setViewName("view/error");
		return mav;
	}
	
}















