// --------------------Support board
// 해당 아티스트의 아이디
var artist_id = document.getElementById('artistId').value;
// 해당 아티스트의 닉네임
var artist_nickname = document.getElementById('artistNickname').value; 
// 로그인한 유저의 아이디
var login_id = document.getElementById('loginId').value;
// 로그인한 유저의 닉네임
var login_nickname = document.getElementById('loginNickname').value;
// support_content
var supportContent = document.getElementsByClassName('support-content')[0];

function list(){
	$.ajax({
        type : "post",
        url : "supportList.do?member_id="+artist_id,
        success : function(result) {
        	supportContent.innerHTML = result;
        }
    });
}

// 글 작성 - 완료
function insertSupport(e){
	var target = e.parentNode.previousSibling.previousSibling;
	var member_id = artist_id;
	var member_nickname = artist_nickname;
	var support_content = target.querySelector('#support_content').value;
	
	$.ajax({
	        type : "post",
	        url : "supportInsert.do",
	        headers : {
	            "Content-Type" : "application/json"
	        },
	        data : JSON.stringify({
	            member_id : member_id,
	            member_nickname : member_nickname,
	            support_content : support_content,
	            writer_id : login_id,
	            writer_nickname : login_nickname
	        }),
	        success : function(result) {
	        	listRest(artist_id);
	        }
	    });
}


// 글 삭제 - 완료
function deleteSupport(e){
	var target = e.nextSibling.nextSibling;
	var support_no = target.value;
	
 	var res = confirm('삭제하시겠습니까?');
 	if(res){
 		$.ajax({
 			type : "post",
 			url : "supportDelete.do?" + support_no,
 			headers : {
 				"Content-Type" : "application/json"
 			},
 			dateType : "text",
 			data : JSON.stringify({
 				support_no : support_no
 			}),
 			success : function(res) {
 				if(res>0){
 					listRest(artist_id);
 				}else{
 					alert('댓글이 있는 게시물은 삭제할 수 없습니다');
 				}
 			}
 		});
 	}
}


// 글 수정
function updateSupport(e){
	var target = e.parentNode.parentNode;
	var res = target.getElementsByClassName('change')[0];
	res.removeAttribute("readonly"); 
 	res.classList.add("changeActive");
 	e.style.display = "none";
 	target.getElementsByClassName('updateRes')[0].style.display = "block";
}

function updateRes(e){
	var target = e.parentNode.parentNode;
	var res = target.getElementsByClassName('change')[0];
	var support_content = target.getElementsByClassName('change')[0].value;
	var support_no = target.getElementsByClassName('supportNo')[0].value;
	
	var done = confirm('수정하시겠습니까?');
	
	if(done){
	$.ajax({
		type : "post",
		url : "supportUpdate.do?suppport_no=" + support_no,
		headers : {
			"Content-Type" : "application/json"
		},
		dateType : "text",
		data : JSON.stringify({
			support_no : support_no,
			support_content : support_content
		}),
		success : function(result) {
			support_content = result.support_content;
			res.classList.remove("changeActive"); 
			res.readOnly = true; 
			e.style.display = "none";
			target.getElementsByClassName('updateBtn')[0].style.display = "block";
		}
	})
	}
}


// 내가 작성한 글
function mine(){
	$.ajax({
		type : "post",
		url : "mylist.do",
		headers : {
			"Content-Type" : "application/json"
		},
		dateType : "text",
		data : JSON.stringify({
			member_id : artist_id,
			writer_id : login_id
		}),
		success : function(result) {
			supportContent.innerHTML = result;
		}
	})
}

// 게시글 리스트 출력
function listRest(artist_id) {
	$.ajax({
		type : "get",
		url : "supportList.do?member_id="+artist_id,
		success : function(result) {

			supportContent.innerHTML = result;
		}
	});
}

// 댓글 보기
function showReply(e){
	var target = e.parentNode.parentNode;

	var res = target.getElementsByClassName('replyArea')[0];
	res.classList.add('replyActive');
	
	e.style.display = "none";
	target.getElementsByClassName('closeReply')[0].style.display ="inherit";
	
	var support_no = target.getElementsByClassName('supportNo')[0].value;
	
	$.ajax({
		type : "post",
		url : "showList.do?support_no=" + support_no,
		headers : {
			"Content-Type" : "application/json"
		},
		data : JSON.stringify({
			support_no : support_no,
			member_id : artist_id
		}),
		success : function(result) {
			res.innerHTML = result;
		}
	});
	
}

//댓글 닫기
function closeReply(e){
var target = e.parentNode.parentNode;
	
	var res = target.getElementsByClassName('replyArea')[0];
	res.classList.remove('replyActive');
	
	e.style.display = "none";
	target.getElementsByClassName('showReply')[0].style.display ="block";
}



// 댓글입력
function replyInsert(e){
	var target = e.parentNode.parentNode.querySelector('.replyContent');
	var comm_content = target.value;
	var member_id = login_id;
	var member_nickname = login_nickname;
	var supportTarget =e.parentNode.parentNode.parentNode.parentNode;
	var support_no = supportTarget.parentNode.querySelector('.supportNo').value;
	$.ajax({
     type : "post",
     url : "commInsert.do",
     headers : {
			"Content-Type" : "application/json"
		},
		dateType : "text",
		data : JSON.stringify({
			member_id : member_id,
			comm_content : comm_content,
			support_no : support_no,
			member_nickname : member_nickname
		}),
		success: function(result){
			replyRest(support_no, supportTarget, member_id);
		}
 })
}

// 댓글수정
function replyUpdate(e){
	e.style.display = "none";
	var doneBtn = e.parentNode.getElementsByClassName('updateDone')[0];
	doneBtn.style.display = "block";
	
	var content = e.parentNode.parentNode.querySelector('.comm_content');
	content.readOnly = false;
	content.classList.add('commActive');
}

function updateDone(e){
	var comm_no = e.parentNode.querySelector('.replyNum').value;
	var target= e.parentNode.parentNode.querySelector('.comm_content');
	var comm_content = target.value;
	
	var res = confirm('수정하시겠습니까?');
	if(res){
	$.ajax({
     type : "post",
     url : "commUpdate.do?comm_no="+comm_no,
     headers : {
			"Content-Type" : "application/json"
		},
		dateType : "text",
		data : JSON.stringify({
			comm_no : comm_no,
			comm_content : comm_content
		}),
		success: function(result){
		}
 }).done(function(){
 	e.style.display ="none";
 	var updateBtn = e.parentNode.getElementsByClassName('replyUpdate')[0];
 	updateBtn.style.display = "block";
 	target.readOnly = true;
 	target.classList.remove('commActive');
 });
	}
};

// 댓글삭제
function replyDelete(e){
	var res = confirm('삭제하시겠습니까?');
	var supportTarget =e.parentNode.parentNode.parentNode.parentNode;
	if(res){
		var comm_no = e.parentNode.querySelector('.replyNum').value;
		var support_no = e.parentNode.querySelector('.supportNum').value;
		
		$.ajax({
		        type : "post",
		        url : "commDelete.do?comm_no="+comm_no,
		        success : function(result) {
		        	replyRest(support_no, supportTarget, artist_id);
		        }
		    });
	}
}

// 댓글 rest 출력
function replyRest(support_no, supportTarget, member_id){
	$.ajax({
		type : "post",
		url : "showList.do?support_no=" + support_no,
		headers : {
			"Content-Type" : "application/json"
		},
		data : JSON.stringify({
			support_no : support_no,
			member_id : member_id
		}),
		success : function(result) {
			supportTarget.querySelector('.replyArea').innerHTML = result;
		}
	});
}

// 신고하기 --
function report(e){
	var target = e.parentNode.parentNode;
	var receive_id = target.getElementsByClassName('writerId')[0].value;
	var receive_nickname = target.getElementsByClassName('writerNick')[0].innerText;
	open("report.do?receive_id="+receive_id+"&receive_nickname="+receive_nickname, "","width=700, height=550");
}
