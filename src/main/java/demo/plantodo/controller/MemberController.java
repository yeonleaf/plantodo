package demo.plantodo.controller;

import demo.plantodo.domain.Member;
import demo.plantodo.domain.PermStatus;
import demo.plantodo.domain.Settings;
import demo.plantodo.form.MemberJoinForm;
import demo.plantodo.form.MemberLoginForm;
import demo.plantodo.service.MemberService;
import demo.plantodo.service.SettingsService;
import demo.plantodo.validation.MemberJoinlValidator;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;
    private final SettingsService settingsService;
    private final MemberJoinlValidator memberJoinlValidator;

    @GetMapping(value = "/join")
    public String createJoinForm(Model model) {
        model.addAttribute("memberJoinForm", new MemberJoinForm());
        return "member/join-form";
    }

    @PostMapping(value = "/join")
    public String joinMember(@Validated @ModelAttribute("memberJoinForm") MemberJoinForm memberJoinForm,
                             BindingResult bindingResult,
                             Model model) {
        /*검증 코드*/
        /*null값 검증을 통과하지 못하면 이전 페이지로 돌려보내기*/
        if (bindingResult.hasErrors()) {
            return "member/join-form";
        }

        /*이메일/패스워드 형식 검증*/
        memberJoinlValidator.validate(memberJoinForm, bindingResult);
        if (bindingResult.hasErrors()) {
            return "member/join-form";
        }

        /*이미 가입이 된 이메일이면 이전 페이지로 돌려보내기*/
        List<Member> memberByEmail = memberService.getMemberByEmail(memberJoinForm.getEmail());
        if (!memberByEmail.isEmpty()) {
            bindingResult.rejectValue("email", "duplicate");
        }
        if (bindingResult.hasErrors()) {
            return "member/join-form";
        }

        PermStatus status = memberJoinForm.getPermission() != null ? PermStatus.GRANTED : PermStatus.DENIED;
        System.out.println("status : " + status);
        Settings settings = new Settings(status);
        settingsService.save(settings);

        Member member = new Member(memberJoinForm.getEmail(), memberJoinForm.getPassword(), memberJoinForm.getNickname(), settings);
        memberService.save(member);

        System.out.println("permission : " + memberJoinForm.getPermission());
        model.addAttribute("memberLoginForm", new MemberLoginForm());
        return "redirect:/member/login";
    }


    @GetMapping("/login")
    public String createLoginForm(Model model) {
        model.addAttribute("memberLoginForm", new MemberLoginForm());
        return "member/login-form";
    }

    @PostMapping("/login")
    public String loginMember(@Validated @ModelAttribute("memberLoginForm") MemberLoginForm memberLoginForm,
                              BindingResult bindingResult,
                              HttpServletRequest request,
                              HttpServletResponse response) {
        /*null값 체크 후 에러가 발생했을 경우 이전 페이지로 돌아가기*/
        if (bindingResult.hasErrors()) {
            return "member/login-form";
        }

        /*해당 이메일로 가입된 계정이 있는지 확인*/
        List<Member> findMember = memberService.getMemberByEmail(memberLoginForm.getEmail());

        if (findMember.isEmpty()) {
            bindingResult.rejectValue("email", "invalid");
        }

        if (bindingResult.hasErrors()) {
            return "member/login-form";
        }

        /*패스워드가 맞는지 확인*/
        String candidate = memberLoginForm.getPassword();
        Member rightMember = findMember.get(0);

        if (!candidate.equals(rightMember.getPassword())) {
            bindingResult.rejectValue("password", "invalid");
        }

        if (bindingResult.hasErrors()) {
            return "member/login-form";
        }

        HttpSession session = request.getSession();
        session.setAttribute("memberId", rightMember.getId());
        session.setAttribute("nickname", rightMember.getNickname());

        /*마감 알람 on-off 여부 확인*/
        Settings settings = memberService.findOne(rightMember.getId()).getSettings();

        /*혹시 alarmStart 쿠키가 아직 있는 경우 삭제*/
        Cookie pastAlarmStart = new Cookie("deadline_alarm_term", null);
        pastAlarmStart.setMaxAge(0);
        response.addCookie(pastAlarmStart);

        if (settings.isDeadline_alarm()) {
            session.setAttribute("deadline_alarm_term", settings.getDeadline_alarm_term());
        }

        /*로그인 세션 유지 시간 (임의 변경 가능)*/
        session.setMaxInactiveInterval(300);
        return "redirect:/home";
    }

    @GetMapping("/logout")
    public String logoutMember(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.invalidate();
        return "redirect:/";
    }
}
