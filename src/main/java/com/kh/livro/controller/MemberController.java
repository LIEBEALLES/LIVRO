package com.kh.livro.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kh.livro.biz.MemberBiz;
import com.kh.livro.dto.CalendarDto;
import com.kh.livro.dto.MemberDto;
import com.kh.livro.utils.LoginGoogleBO;
import com.kh.livro.utils.LoginNaverBO;
import com.kh.livro.utils.MailService;
import com.kh.livro.utils.PwSHA256;
import com.kh.livro.utils.RandomCode;

@Controller
public class MemberController {

	private static final LoginGoogleBO loginNaverBO = null;

	private Logger logger = LoggerFactory.getLogger(MemberController.class);

	@Autowired
	private MemberBiz memberBiz;
	@Autowired
	private LoginNaverBO loginnaverBO;

	@Autowired
	private LoginGoogleBO logingoogleBO;

	@Autowired
	private MailService mailService;

	@Autowired
	private void setLoginNaverBO(LoginNaverBO loginNaverBO) {
		this.loginnaverBO = loginNaverBO;
	}

	@Autowired
	private void setLoginGooleBO(LoginGoogleBO loginGoogleBO) {
		this.logingoogleBO = loginGoogleBO;
	}

	@RequestMapping("/loginForm.do")
	public String login(Model model, HttpSession session) {
		logger.info("[loginForm.do]");

		String naverAuthUrl = loginnaverBO.getAuthorizationUrl(session);
		logger.info("네이버 : " + naverAuthUrl);

		String googleAuthUrl = logingoogleBO.getAuthorizationUrl(session);
		logger.info("구글 : " + googleAuthUrl);

		model.addAttribute("naver", naverAuthUrl);
		model.addAttribute("google", googleAuthUrl);

		return "login/login";
	}

	@RequestMapping(value = "/login.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> login(@RequestBody MemberDto dto, HttpSession session, Model model) {
		logger.info("[login.do]");

		// member_id를 가지고가서 아이디랑 비밀번호를 res에 담아서 갖고옴
		MemberDto res = memberBiz.encryptchk(dto.getMember_id());
		String encryPassword = dto.getMember_pw();
		System.out.println(encryPassword);

		if (res.getMember_pw().length() > 15) {
			encryPassword = PwSHA256.encrypt(encryPassword);
			dto.setMember_pw(encryPassword);
		}

		MemberDto login = memberBiz.selectOne(dto);
		
		String member_id = dto.getMember_id();
		
		List<CalendarDto> calList = memberBiz.showNoti(member_id);
		System.out.println("맴버 아이디는? : "+member_id);
		session.setAttribute("calList", calList);
		
		boolean check = false;
		if (login != null) {
			session.setAttribute("logindto", login);
			check = true;

		}

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("check", check);
		map.put("dto", login);

		return map;

	}

	@RequestMapping(value = "/success.do")
	public String success(HttpServletRequest request, RedirectAttributes redirectAttributes, HttpSession session,
			Model model) {
		logger.info("[success.do]");

		String referer = request.getHeader("Referer");

		logger.info(referer);
		
		if (referer.contains("broadDetail.do")) {
			return "redirect:" + referer;
		}
		return "main/main";
	}

	@RequestMapping("/logout.do")
	public String logout(HttpSession session, Model model) {

		session.invalidate();

		model.addAttribute("msg", "성공적으로 로그아웃하였습니다.");
		model.addAttribute("uri", "/");

		return "redirect";
	}

	@RequestMapping("/registForm.do")
	public String regist() {
		logger.info("[registForm.do]");
		return "regist/regist";
	}

	@RequestMapping("/join.do")
	public String registRes(MemberDto dto, Model model, String member_addr1, String member_addr2, String member_addr3) {
		logger.info("[join.do]");
		// 주소 db에 넣어주기
		String member_addr = member_addr1 + " " + member_addr2 + " " + member_addr3;
		dto.setMember_addr(member_addr);

		logger.info("[암호화 안된 패스워드 :]" + dto.getMember_pw());
		logger.info("[암호화 안된 패스워드 체크 :]" + dto.getMember_pwchk());

		// 비밀번호 암호화
		String encryPassword = PwSHA256.encrypt(dto.getMember_pw());
		String encryPassword_chk = PwSHA256.encrypt(dto.getMember_pwchk());

		dto.setMember_pw(encryPassword);
		dto.setMember_pwchk(encryPassword_chk);

		logger.info("[암호화 된 패스워드 : ]" + dto.getMember_pw());
		logger.info("[암호화 된 패스워드 체크 :]" + dto.getMember_pwchk());

		int res = memberBiz.join(dto);
		if (res > 0) {
			model.addAttribute("msg", "회원가입을 축하드립니다. 로그인해주세요!");
			model.addAttribute("url", "/loginForm.do");
			return "redirect";
		}
		model.addAttribute("msg", "회원가입에 실패하셨습니다.");
		model.addAttribute("url", "/registForm.do");
		return "redirect";

	}

	// 아이디 중복체크
	@ResponseBody
	@RequestMapping(value = "/idChk.do", method = RequestMethod.POST)
	public int idChk(MemberDto dto) {
		int res = 0;
		res = memberBiz.idChk(dto);

		return res;
	}

	// 닉네임 중복체크
	@ResponseBody
	@RequestMapping(value = "/nickChk.do", method = RequestMethod.POST)
	public int nickChk(MemberDto dto) {
		int res = 0;
		res = memberBiz.nickChk(dto);
		return res;
	}

	// 네이버 로그인 성공시 callback호출 메소드
	@RequestMapping(value = "/callback.do", method = { RequestMethod.GET, RequestMethod.POST })
	public String navercallback(@RequestParam String code, @RequestParam String state, HttpSession session)
			throws IOException, InterruptedException, ExecutionException {

		// 로그인 사용자 정보를 읽어온다.
		OAuth2AccessToken oauthToken = loginnaverBO.getAccessToken(session, code, state);
		String apiResult = loginnaverBO.getUserProfile(oauthToken);

		logger.info(" apiResult : " + apiResult);

		JsonObject object = JsonParser.parseString(apiResult).getAsJsonObject().get("response").getAsJsonObject();

		MemberDto naverdto = new MemberDto();

		naverdto.setMember_id(object.get("email").toString().split("\"")[1]);
		naverdto.setMember_pw(object.get("id").toString().split("\"")[1]);
		naverdto.setMember_email(object.get("email").toString().split("\"")[1]);
		naverdto.setMember_name(object.get("name").toString().split("\"")[1]);

		logger.info(">>>naverdto :" + naverdto);

		MemberDto res = memberBiz.selectOne(naverdto);
		if (res == null) {
			session.setAttribute("snsinfo", naverdto);

			return "regist/snsregist";
		} else {

			session.setAttribute("logindto", res);

			return "main/main";
		}
	}

	@RequestMapping(value = "/gcallback.do", method = RequestMethod.GET)
	public String googlecallback(@RequestParam String code, @RequestParam String state, HttpSession session)
			throws IOException, InterruptedException, ExecutionException {

		logger.info(">> [CONTROLLER-USERINFO] GOOGLE callback");
		// 사용자 정보 받아옴.
		OAuth2AccessToken oauthToken = logingoogleBO.getAccessToken(session, code, state);
		logger.info("@@@@ session : " + session + "  code : " + code + "   state : " + state);

		String google = logingoogleBO.getUserProfile(oauthToken);
		logger.info("@@@ apiResult : " + google);

		MemberDto googledto = new MemberDto();

		JsonObject object = JsonParser.parseString(google).getAsJsonObject();

		googledto.setMember_id(object.get("email").toString().split("\"")[1]);
		googledto.setMember_pw(object.get("email").toString().split("\"")[1]);
		googledto.setMember_email(object.get("email").toString().split("\"")[1]);
		googledto.setMember_name(object.get("name").toString().split("\"")[1]);

		MemberDto result = memberBiz.selectOne(googledto);

		if (result == null) {
			session.setAttribute("snsinfo", googledto);

			return "regist/snsregist";
		} else {
			session.setAttribute("logindto", result);
			return "main/main";
		}
	}

	// 아이디 비밀번호 찾기 창으로
	@RequestMapping(value = "/findForm.do")
	public String Find() {

		return "find/find";
	}

	// 아이디 찾는 창
	@RequestMapping(value = "/idchk.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> id(@RequestBody MemberDto dto, HttpSession session, Model model) {
		logger.info(">>>>>>>>[id.chk.do]");

		logger.info("NAME" + dto.getMember_name());
		logger.info("PHONE" + dto.getMember_phone());

		MemberDto res = memberBiz.idfind(dto);
		logger.info("[res]>>>>>" + res);

		boolean check = false;
		// 입력받은 값이 있다면
		Map<String, Object> map = new HashMap<String, Object>();
		if (res != null) {

			map.put("member_id", res.getMember_id());
			check = true;

		}

		map.put("check", check);

		return map;

	}
	   //아이디 찾기 후 리턴 
	   @RequestMapping(value = "/goid.do")
	   public String goid(MemberDto dto, HttpSession session, Model model) {
	      logger.info(">>>>>goid.do");
	      
	      
	      return "find/find";
	   }

	// sns 로그인
	@RequestMapping(value = "/snsjoin.do", method = { RequestMethod.POST, RequestMethod.GET })
	public String snsjoin(MemberDto dto, Model model, String member_addr1, String member_addr2, String member_addr3) {
		logger.info(">>>[SNS sign up]");

		String member_addr = member_addr1 + " " + member_addr2 + " " + member_addr3;
		dto.setMember_addr(member_addr);

		int res = memberBiz.snsjoin(dto);

		if (res > 0) {

			model.addAttribute("msg", "회원가입을 축하드립니다. 로그인해주세요!");
			model.addAttribute("url", "/loginForm.do");

			return "redirect";
		} else {
			model.addAttribute("msg", "회원가입에 실패하셨습니다.");

			return "login/login";
		}
	}
	// 비밀번호 찾기

	@RequestMapping(value = "/pwchk.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> pw(@RequestBody MemberDto dto, HttpSession session, Model model) {
		logger.info(">>>>>>>>[pw.chk.do]");

		logger.info("ID    " + dto.getMember_id());
		logger.info("EMAIL   " + dto.getMember_email());

		String member_email = dto.getMember_email();

		MemberDto res = memberBiz.pwfind(dto);
		logger.info("[res]>>>>>" + res);

		boolean check = false;
		// 입력받은 값이 있다면

		Map<String, Object> map = new HashMap<String, Object>();
		if (res != null) {

			map.put("member_id", res.getMember_id());
			map.put("member_email", res.getMember_email());
			check = true;

		}

		RandomCode randomcode = new RandomCode();
		// 랜덤코드를 생성해주는 메소드 호출
		String ran = randomcode.excuteGenerate();
		logger.info("1." + ran);
		session.setAttribute("pw", ran);
		String subject = "비밀번호 찾기 인증 코드 발급 안내입니다.";
		StringBuilder sb = new StringBuilder();
		sb.append("귀하의 인증 코드는 " + ran + " 입니다.");
		// 이메일 전송
		mailService.send(subject, sb.toString(), "dywjdal1995@naver.com", member_email, null, ran);
		System.out.println(ran);
		// 발송된 랜덤코드 리턴

		map.put("check", check);
		map.put("ran", ran);

		return map;

	}

	// 비밀번호 변경창으로
	@RequestMapping(value = "/pwupdate.do")
	public String pwupdate(Model model, String member_id, HttpSession session) {

		logger.info("member_id :" + member_id);

		session.getAttribute(member_id);
		model.addAttribute("member_id", member_id);

		logger.info(">>>>> Controller PWupdate");

		return "find/pwupdate";

	}

	// 비밀번호 변경 팝업창
	@RequestMapping(value = "/pwupdateres.do", method = { RequestMethod.POST, RequestMethod.GET })
	public String pwupdateres(@RequestParam(value = "member_id") String member_id,
			@RequestParam(value = "member_pw") String member_pw, Model model, HttpSession session) {

		MemberDto member = new MemberDto();

		member.setMember_id(member_id);
		member.setMember_pw(member_pw);

		logger.info(">>>ID : " + member.getMember_id());
		logger.info(">>>PW : " + member.getMember_pw());

		int res = memberBiz.pwupdate(member);

		if (res > 0) {
			model.addAttribute("msg", "변경완료!");
			model.addAttribute("url", "/loginForm.do");
			return "redirect";

		} else {
			model.addAttribute("msg", "변경실패..");

			return "find/pwupdate";
		}

	}

	// USER 회원정보수정
	@RequestMapping(value = "/userupdate.do", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public Map<String, Boolean> userupdate(@RequestBody MemberDto dto, Model model, HttpSession session) {

		logger.info("!!!!DTO : " + dto);

		int res = memberBiz.userupdate(dto);

		logger.info(">>>> updateDto : " + dto);

		logger.info(">>>USER RES : " + res);

		MemberDto rdto = new MemberDto();

		rdto.setMember_id(dto.getMember_id());
		rdto.setMember_pw(dto.getMember_pw());
		rdto.setMember_email(dto.getMember_email());
		rdto.setMember_nickname(dto.getMember_nickname());
		rdto.setMember_addr(dto.getMember_addr());
		rdto.setMember_phone(dto.getMember_phone());
		rdto.setMember_role(dto.getMember_role());

		System.out.println(rdto.getMember_role() + "확");

		boolean check = false;
		// 입력받은 값이 있다면
		if (res > 0) {
			session.setAttribute("logindto", rdto);

			check = true;
		}

		Map<String, Boolean> map = new HashMap<String, Boolean>();
		map.put("check", check);

		return map;
	}

	
	// USER 회원정보수정
		@RequestMapping(value = "/arupdate.do", method = { RequestMethod.POST, RequestMethod.GET })
		@ResponseBody
		public Map<String, Boolean> arupdate(@RequestBody MemberDto dto, Model model, HttpSession session) {


			int res = memberBiz.arupdate(dto);

			logger.info(">>>Artist : " + res);

			MemberDto rdto = new MemberDto();

			rdto.setMember_id(dto.getMember_id());
			rdto.setMember_email(dto.getMember_email());
			rdto.setMember_nickname(dto.getMember_nickname());
			rdto.setMember_addr(dto.getMember_addr());
			rdto.setMember_phone(dto.getMember_phone());
			rdto.setMember_role(dto.getMember_role());

			System.out.println(rdto.getMember_nickname() + "확");

			boolean check = false;
			// 입력받은 값이 있다면
			if (res > 0) {
				session.setAttribute("logindto", rdto);

				check = true;
			}

			Map<String, Boolean> map = new HashMap<String, Boolean>();
			map.put("check", check);

			return map;
		}

}
