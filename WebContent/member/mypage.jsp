<%@page import="java.util.List"%>
<%@page import="com.pet.domain.OrderSummary"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%
	Member obj=(Member)request.getAttribute("member");
	List<OrderSummary> orderList=(List)request.getAttribute("orderList");
%>
<!DOCTYPE html>
<html>
<head>
<title>Pet Shop | Contact</title>
<meta charset="UTF-8">
<link href="/css/style.css" rel="stylesheet" type="text/css">
<!--[if IE 6]><link href="/css/ie6.css" rel="stylesheet" type="text/css"><![endif]-->
<!--[if IE 7]><link href="/css/ie7.css" rel="stylesheet" type="text/css"><![endif]-->
</head>
<body>
<div id="header">
	<%@ include file="/include/main_navi.jsp" %>
</div>
<div id="body">
  <div id="content">
    <div class="content">
      <h2>[ MyPage ]</h2>
      <ul class="connect">
        <li>
          <h2>[ 나의 정보 ]</h2>
          <p> 
          	<span>
          		아이디: <%=obj.getId() %>
          	</span> 
          	<span>
          		비밀번호: <input type="password" value="<%=obj.getPassword()%>">
          	</span> 
          	<span>
          		이름: <%=obj.getName() %>
          	</span> 
          	<span>
          		연락처: <%=obj.getPhone() %>
          	</span> 
          	<span>
          		Email: <%=obj.getEmail() %>
          	</span> 
          	<span>
          		주소: <%=obj.getAddr() %>
          	</span> 
          	</p>
        </li>
        <li>
          <h2>[ 결제 내역]</h2>
          <p> 
          	<span>
          		<table width="100%" border="1px">
          			<tr>
          				<td>주문번호</td>
          				<td>총 결제액</td>
          				<td>주문일</td>
          				<td>결제방법</td>
          				<td>받는 분</td>
          			</tr>
          			<%for(int i=0;i<orderList.size();i++){ %>
          			<%OrderSummary orderSummary=orderList.get(i); %>
          			<tr>
          				<td><%=orderSummary.getOrder_summary_id() %></td>
          				<td><%=orderSummary.getTotal_pay() %></td>
          				<td><%=orderSummary.getOrder_date().substring(0,10) %></td>
          				<td><%=orderSummary.getPay_method() %></td>
          				<td><%=orderSummary.getReceiver().getRname() %></td>
          			</tr>
					<%} %>
          		</table>
          	</span> 
          </p>
        </li>
        <li>
          <h2>[ 상담내역 ]</h2>
          <p> <span>Petshop</span> <span>250 Lorem Ipsum Street</span> <span>4th Floor</span> <span>jaonfanr, Caknan 109935</span> <span>Kiangab</span> </p>
        </li>
      </ul>
    </div>
    
  </div>
  <div class="featured">
    <ul>
      <li><a href="#"><img src="/images/organic-and-chemical-free.jpg" width="300" height="90" alt=""></a></li>
      <li><a href="#"><img src="/images/good-food.jpg" width="300" height="90" alt=""></a></li>
      <li class="last"><a href="#"><img src="/images/pet-grooming.jpg" width="300" height="90" alt=""></a></li>
    </ul>
  </div>
</div>
<%@ include file="/include/footer.jsp" %>
</body>
</html>
