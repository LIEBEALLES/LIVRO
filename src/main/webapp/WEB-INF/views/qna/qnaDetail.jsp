<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	request.setCharacterEncoding("UTF-8");
%>
<%
	response.setContentType("text/html charset=UTF-8");
%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet"
	href="https://uicdn.toast.com/editor/latest/tui-editor/dist/tui-editor.css" />
<link rel="stylesheet" href="resources/css/qna_detail.css">
<script type="text/javascript"
	src="https://code.jquery.com/jquery-3.3.1.min.js"></script>
<!-- font awesome -->
<script src="https://kit.fontawesome.com/d28db34e8b.js"
	crossorigin="anonymous" defer></script>
<title>Q&A-글상세</title>
</head>
<script>
$(document).ready(function(){
	//관리자만 댓글 달 수있고 답변 내용이 없으면 return false 해서 submit 이벤트 막음
	$("#reform").submit(function(e){
		var retext = $("#retext").val().trim();
		console.log(retext);
		
		if(${logindto.member_role != 'M' }){
			alert("관리자만 등록할 수 있습니다.");
			return false;
		}else{
			if(retext == null || retext == ''){
				alert("답변을 등록해주세요.")
				return false;
			}else{
				return true;
			}
		}
	})

})
//댓글삭제
function qnareDelete(qnare_no){
	   var qna_no = ${qnadetaildto.qna_no };
	   $.ajax({
	      url : "qnaredelete.do?qna_no=" + qna_no +"&qnare_no=" + qnare_no,
	      type : "get",
	      success : function(res){
	    	  if(res>0){
	    	  location.reload();
	    	  alert("댓글을 삭제하는데 성공하였습니다.");
	    	  }else{
	    		  alert("댓글을 삭제하는데 실패하였습니다.");
	    	  }
	      },
	      error : function(error){
	         alert("댓글을 삭제하는데 실패하였습니다.");
	      }
	   })
	} 
	
//댓글수정
function qnareUpdateForm(e){
	e.style.display = "none";
	var doneBtn = e.parentNode.getElementsByClassName("qnare_update_res_btn")[0];
	doneBtn.style.display ="inline-block";
	
	$(".qnare_content").attr("readonly", false);
	$(".qnare_content").css("background-color", "white");
	
}
//댓글완료버튼 번호, content 가지고 감
function qnareUpdateDone(qnare_no){
	var qnare_content = $(".qnare_content").val();
	var obj={ 
		 "qnare_content" : qnare_content,
		 "qnare_no" : qnare_no
	}
	$.ajax({
		url: "qnareupdate.do",
		type : "post",
		data :obj,
		dataType: "json",		
		success : function(res){
			if(res>0){
				location.reload();
				alert("답변이 수정되었습니다.")
			}else{
				alert("답변을 수정하는데 실패했습니다.")
			}
		},
		error : function(error){
		alert("통신실패");
		}
	})
}
</script>
<body>

	<!-- 본문내용  -->
	<jsp:include page="/WEB-INF/views/header/header.jsp" />
	<div class="qna_detail_wrap">
		<p>
			<i class="far fa-comment-dots"></i>Q&A게시판
		</p>

		<div class="qna_title_wrap">
			<c:if test="${qnadetaildto.qna_secret =='Y' }">
				<i class="fas fa-lock"></i>
			</c:if>
			${qnadetaildto.qna_title }
		</div>

		<div class="qna_nick_wrap">${qnadetaildto.member_nickname }
			<fmt:formatDate value="${qnadetaildto.qna_regdate }"
				pattern="yy-MM-dd HH:mm" />
		</div>


		<div class="qna_content_wrap">
			<div id="viewer"></div>
		</div>

		<div class="qna_reply_header">Q&A답변</div>
		<!-- 댓글목록 -->
		<div class="qna_reply_wrap">
			<c:choose>
				<c:when test="${empty qnarelist }">
					<div>
						<div>작성된 답변이 없습니다.</div>
					</div>
				</c:when>
				<c:otherwise>
					<!-- 댓글이 있을 때 보여짐 -->
					<c:forEach items="${qnarelist }" var="qnarelist">
						<div class="qna_reply_area">
							<div class="qna_reply_nick">${qnarelist.member_nickname }</div>
							<div class="qna_reply_format_date">
								<fmt:formatDate value="${qnarelist.qnare_regdate }"
									pattern="yyyy.MM.dd" />
							</div>
							<!--관리자에게만 보이는 수정,삭제버튼 -->
							<div class="qna_reply_content">
								<textarea readonly="readonly" class="qnare_content">${qnarelist.qnare_content }</textarea>
								<c:if test="${logindto.member_role eq 'M' }">
									<input type="button" value="수정" onclick="qnareUpdateForm(this)"
										class="qnare_btns" />
									<input type="button" value="완료"
										onclick="qnareUpdateDone(${qnarelist.qnare_no})"
										class="qnare_update_res_btn qnare_btns" />
									<input type="hidden" name="qna_no"
										value="${qnadetaildto.qna_no }">
									<input type="hidden" name="qnare_no"
										value="${qnarelist.qnare_no }">
									<input type="button" value="삭제"
										onclick="qnareDelete(${qnarelist.qnare_no})"
										class="qnare_btns" />
								</c:if>
							</div>
						</div>
					</c:forEach>
				</c:otherwise>
			</c:choose>
			<!-- 댓글입력창 -->
			<!-- 댓글이 하나라도 있으면 못 달게 막아버리기  -->
			<div id="qnare_reply_input">
				<c:choose>
					<c:when test="${empty qnarelist }">

						<form action="qnareinsert.do" method="post" id="reform">
							<input type="hidden" name="qna_no"
								value="${qnadetaildto.qna_no }"> <input type="hidden"
								name="member_nickname" value="${logindto.member_nickname }">
							<div class="qna_input_reply">
								<textarea placeholder="댓글은 관리지만 등록할 수 있습니다."
									name="qnare_content" id="retext"></textarea>
								<input type="submit" value="입력" class="replyBtn qnare_btns">
							</div>
						</form>
					</c:when>
				</c:choose>
			</div>

		</div>

		<!-- 글쓴이만 볼 수 있는 수정,삭제버튼 + 목록으로 돌아가기 -->
		<div class="qna_btn">
			<input type="button" value="목록" onclick="location.href='qnalist.do'"
				class="qnare_btns">
			<c:if
				test="${logindto.member_nickname eq qnadetaildto.member_nickname}">

				<input type="button" value="수정"
					onclick="location.href='qnaupdateform.do?qna_no=${qnadetaildto.qna_no}'"
					class="qnare_btns">
				<input type="button" value="삭제"
					onclick="location.href='qnadelete.do?qna_no=${qnadetaildto.qna_no }'"
					class="qnare_btns">

			</c:if>

		</div>

	</div>

	<script
		src="https://uicdn.toast.com/editor/latest/toastui-editor-viewer.js"></script>
	<script>
		const viewer = new toastui.Editor({
			el : document.querySelector('#viewer'),
			initialValue : `${qnadetaildto.qna_content }`
		});
	</script>
	<!-- <script type="text/javascript" src="resources/js/qnaDetail.js"></script> -->
<jsp:include page="/WEB-INF/views/footer/footer.jsp" /> 	
</body>
</html>