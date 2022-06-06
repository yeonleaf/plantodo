package demo.plantodo.interceptor;

import demo.plantodo.controller.HomeController;
import demo.plantodo.domain.PlanTerm;
import demo.plantodo.form.CalendarSearchForm;
import demo.plantodo.service.MemberService;
import demo.plantodo.service.PlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HomeRenderInterceptor implements HandlerInterceptor {

    private final MemberService memberService;
    private final PlanService planService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        beforeHome(request);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        String requestURI = request.getRequestURI();
        beforeHome(request);
    }

    private void beforeHome(HttpServletRequest request) {
        /*달력 렌더링*/
        LocalDate now = LocalDate.now();
        int yearValue = now.getYear();
        int monthValue = now.getMonthValue();
        int length = now.lengthOfMonth();
        CalendarSearchForm cSearchForm = new CalendarSearchForm(yearValue, monthValue);
        LocalDate[][] calendar = cSearchForm.makeCalendar(yearValue, monthValue, length);

        /*today - urgent plans*/
        List<PlanTerm> urgentPlans = planService.findUrgentPlanTerms(memberService.getMemberId(request));
        System.out.println("[urgentPlans] : " + urgentPlans.size());

        /*set attributes*/
        request.setAttribute("urgentPlans", urgentPlans);
        request.setAttribute("today", now);
        request.setAttribute("calendarSearchForm", cSearchForm);
        request.setAttribute("calendar", calendar);
    }
}
