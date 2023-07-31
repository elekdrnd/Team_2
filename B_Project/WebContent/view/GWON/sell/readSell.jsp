<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%-- <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/core" %> --%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<!-- 부트스트랩 CSS 파일 링크 -->
<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">

<style>
#floating-button {
	margin : 2px;
}

.col-3 {
   text-align : center;
}
</style>

<script>
 function readURL(obj){
	  if (obj.files && obj.files[0]) { //파일이 있드면
	         var reader = new FileReader(); //FileReader()객체생성
	         reader.onload = function (e) {
	        	 //id가 preview인 요소의 src속성값을 설정 =>img src속성값을 파일명으로 적용
	        	 $('#preview').attr('src', e.target.result)};
		         reader.readAsDataURL(obj.files[0]);
	 }
 }
 function readURL(obj){
	  if (obj.files && obj.files[0]) { //파일이 있드면
	         var reader = new FileReader(); //FileReader()객체생성
	         reader.onload = function (e) {
	        	 //id가 preview인 요소의 src속성값을 설정 =>img src속성값을 파일명으로 적용
	        	 $('#carouselPic').attr('src', e.target.result)};
		         reader.readAsDataURL(obj.files[0]);
	 }
}
</script>

</head>
<body>
<%@ include file = "../../../header.html" %>



 <div class="container">
	<h2  class="" align = "center">중고거래 글 상세보기</h2>
	
	
	<table class="table table-bordered">
	 <tbody>
	<%-- <c:if test="${not empty sellDTO.sell_file && sellDTO.sell_file = null}"> --%>
	  <tr>
	   <th colspan ="12">
	   	  <input type="hidden" name="originalSell_file" id="originalSell_file" value="${sellDTO.sell_file}" />
	   	  <img src= "<%=request.getContextPath() %>/gwon/sell/sellDownload.do?fileNo=${sellDTO.sell_no}&sell_file=${sellDTO.sell_file}"  id="preview" style="width:250px;"/>
	    </th>
	  </tr>
	<%--   <tr>
	   <th class="col-3"><label for="sell_file">수정용이미지</label></th>
	   <td><input type="file" name="sell_file" id="sell_file" onchange="readURL(this);"/></td>
	  </tr> 
	 </c:if> --%>
	  <tr>
	   <th class="col-3">글번호</th>
	   <td class="col-9">${sellDTO.sell_no}</td>
	  </tr>
	  <tr>
	   <th class="col-3">작성자id</th>
	   <td class="col-9">${sellDTO.user_id}</td>
	  </tr>
	  <tr>
	   <th class="col-3">작성자명</th>
	   <td>${sellDTO.user_name}</td>
	  </tr>
	  <tr>
	   <th class="col-3">제목</th>
	   <td>${sellDTO.sell_title}</td>
	  </tr>
	  <tr>
	   <th class="col-3">글카테고리</th>
	   <td>${sellDTO.sell_category}</td>
	  <tr>
	  <tr>
	   <th class="col-3">가격</th>
	   <td>${sellDTO.sell_price}</td>
	  <tr>
	  <tr>
	   <th class="col-3">위치</th>
	   <td>${sellDTO.sell_loc}</td>
	  <tr>
	   <th class="col-3">작성일</th>
	   <!-- 부트스트랩에서 날짜 form 찾기  -->
	   <td><fmt:formatDate value="${sellDTO.sell_regDate}"   pattern="yyyy년  MM월  dd일"/></td>
	  </tr>
	  <tr>
	   <th class="col-3">수정일</th>
	   	<td><fmt:formatDate value="${sellDTO.sell_modDate}"   pattern="yyyy년  MM월 dd일"/> </td>
	  </tr>
	  <tr>
	   <th class="col-3">조회수</th>
	   <td>${sellDTO.sell_read_cnt}</td>
	  </tr>
	  <tr>
	   <th class="col-3">내용</th>
	   <td>${sellDTO.sell_content}</td>
	  </tr>
	  
	 </tbody>	
	</table>

	<div class="">
	 <c:if test="${AUTH_USER.user_id eq sellDTO.user_id}">
	   <a id = "floating-button" class="btn btn-secondary" href="/gwon/sell/sellDelete.do?no=${sellDTO.sell_no}" style = "float: right; "role="button">삭제</a>
	 </c:if> 
	 
	 <c:if test="${AUTH_USER.user_id eq sellDTO.user_id}">
	   <a id = "floating-button" class="btn btn-success" href="/gwon/sell/sellModify.do?no=${sellDTO.sell_no}" style = "float: right; "role="button">수정</a>
     </c:if> 

	 <c:set var="pageNo" value="${empty param.pageNo?1:param.pageNo}" />
 	   <a id = "floating-button" class="btn btn-secondary" href="/gwon/sell/sellList.do?pageNo=${pageNo}" style = "float: right; "role="button">목록</a>
	</div>
	
<form method="post" action="/gwon/sell/sellLike.do" >	
    <input type = "hidden" name="no" value = "${sellDTO.sell_no}"></input>
	<div class =""></div>
		<c:if test="${AUTH_USER.user_id eq sellDTO.user_id}">
			<button type = "submit" class="btn btn-success"><img src = "../../../image/heart.svg"/>&nbsp;좋아요</button>
			<!-- <button type = "submit" class = "btn btn-secondary" style = "clear : both">좋아요</button> -->
		 </c:if> 
		<span>&nbsp;<img src = "../../../image/heart-fill.svg"/>&nbsp;&nbsp;+&nbsp;${sellDTO.sell_fav}</span>
	</div>
</form>


</body>


<!-- 부트스트랩 JS 파일들 링크 -->
<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.9.1/dist/umd/popper.min.js"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
</html>