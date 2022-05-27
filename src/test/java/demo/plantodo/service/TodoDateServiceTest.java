package demo.plantodo.service;

import demo.plantodo.domain.*;
import demo.plantodo.repository.MemberRepository;
import demo.plantodo.repository.PlanRepository;
import demo.plantodo.repository.TodoDateRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@SpringBootTest
@Transactional
class TodoDateServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private TodoDateRepository todoDateRepository;

    @Autowired
    private TodoDateService todoDateService;

    @Test
    public void todoDateDailyDeleteTest() throws Exception {
        //given
        /*member 저장*/
        Member member = new Member("test@abc.co.kr", "abc123!@#", "test");
        memberRepository.save(member);

        /*plan 저장*/
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(3);
        PlanTerm plan = new PlanTerm(member, PlanStatus.NOW, start, "plan1", end);
        planRepository.saveTerm(plan);

        /*todoDate 저장*/
        TodoDateDaily todoDate = new TodoDateDaily(TodoStatus.UNCHECKED, start, "todoDate1", plan);
        todoDateRepository.save(todoDate);

        //when
        todoDateRepository.deleteDaily(todoDate);

        //then
        Assertions.assertThat(planRepository.findOne(plan.getId()).getUnchecked_TodoDate_cnt()).isEqualTo(0);
    }

    @Test
    public void todoDateSwitchTest() throws Exception {
        //given
        /*member 저장*/
        Member member = new Member("test@abc.co.kr", "abc123!@#", "test");
        memberRepository.save(member);

        /*plan 저장*/
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(3);
        PlanTerm plan = new PlanTerm(member, PlanStatus.NOW, start, "plan1", end);
        planRepository.saveTerm(plan);

        /*todoDate 저장*/
        TodoDateDaily todoDate = new TodoDateDaily(TodoStatus.UNCHECKED, start, "todoDate1", plan);
        todoDateService.save(todoDate);

        System.out.println("[변경 전 uncheckedCnt] : " + planRepository.findOne(plan.getId()).getUnchecked_TodoDate_cnt());

        //when
        /*todoDate 상태 변경*/
        todoDateService.switchStatusDaily(todoDate.getId());

        //then
        Assertions.assertThat(planRepository.findOne(plan.getId()).getChecked_TodoDate_cnt()).isEqualTo(1);
        Assertions.assertThat(planRepository.findOne(plan.getId()).getUnchecked_TodoDate_cnt()).isEqualTo(0);

    }
}