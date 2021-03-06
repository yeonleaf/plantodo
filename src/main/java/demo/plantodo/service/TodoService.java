package demo.plantodo.service;

import demo.plantodo.domain.*;
import demo.plantodo.form.TodoUpdateForm;
import demo.plantodo.repository.PlanRepository;
import demo.plantodo.repository.TodoDateRepository;
import demo.plantodo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoService {
    private final PlanRepository planRepository;
    private final TodoRepository todoRepository;
    private final TodoDateRepository todoDateRepository;
    private final TodoDateService todoDateService;
    private final CommonService commonService;

    public void save(Plan plan, Todo todo) {
        todoRepository.save(todo);

        /*TodoDate 만들기*/
        /*startDate, endDate 정의*/
        LocalDate startDate = plan.getStartDate();
        LocalDate endDate = LocalDate.now();
        if (plan instanceof PlanTerm) {
            PlanTerm planTerm = (PlanTerm) plan;
            endDate = planTerm.getEndDate();
        }

        /*todoDate 만들기*/
        int uncheckedTodoDateCnt = todoDateService.todoDateInitiate(startDate, endDate, todo);
    }

    public Todo findOne(Long todoId) {
        return todoRepository.findOne(todoId);
    }

    public int delete(Long todoId) {
        /*todoDate 모두 불러오기*/
        LocalDate today = LocalDate.now();
        List<TodoDate> todoDateByTodoId = todoRepository.getTodoDateRepByTodoId(todoId);

        /*오늘 날짜 이후의 todoDate에 delete함수를 호출해서 삭제하기*/
        int checkedCnt = 0;
        int uncheckedCnt = 0;
        for (TodoDate todoDate : todoDateByTodoId) {
            if (todoDate.getDateKey().equals(today) || todoDate.getDateKey().isAfter(today)) {
                if (todoDate instanceof TodoDateRep) {
                    todoDateRepository.deleteRep((TodoDateRep) todoDate);
                }

                if (todoDate instanceof TodoDateDaily) {
                    todoDateRepository.deleteDaily((TodoDateDaily) todoDate);
                }

                /*todoDate가 checked상태면 checkedCnt, unchecked상태면 uncheckedCnt에 더한다.*/
                if (todoDate.getTodoStatus().equals(TodoStatus.CHECKED)) {
                    checkedCnt += 1;
                }
                if (todoDate.getTodoStatus().equals(TodoStatus.UNCHECKED)) {
                    uncheckedCnt += 1;
                }

            }
        }

        System.out.println("[checkedCnt] : " + checkedCnt);
        System.out.println("[uncheckedCnt] : " + uncheckedCnt);

        /*연결된 plan의 checkedCnt와 uncheckedCnt를 원복한다.*/
        Plan plan = findConnectedPlan(todoId);
        planRepository.deleteCheckedAndUnchecked(plan.getId(), checkedCnt, uncheckedCnt);

        /*todo를 삭제한다.*/
        int deleteCnt = checkedCnt + uncheckedCnt;

        if (deleteCnt == todoDateByTodoId.size()) {
            todoRepository.delete(todoId);
        }
        return (todoDateByTodoId.size() - deleteCnt);
    }

    private Plan findConnectedPlan(Long todoId) {
        Todo todo = todoRepository.findOne(todoId);
        return todo.getPlan();
    }

    public void update(TodoUpdateForm todoUpdateForm, Long todoId, Plan plan) {
        /*to-do 찾아오기*/
        Todo todo = todoRepository.findOne(todoId);
        LocalDate today = LocalDate.now();
        /*todoUpdateForm와 to-do의 repOption과 repValue에 변화가 있었는지 확인*/
        if (todo.getRepOption() != todoUpdateForm.getRepOption() || !todo.getRepValue().equals(todoUpdateForm.getRepValue())) {
            /*오늘 이후의 todoDate 삭제*/
            List<TodoDate> todoDateAfterToday = todoRepository.getTodoDateByTodoIdAfterToday(todoId, today);
            for (TodoDate todoDate : todoDateAfterToday) {
                if (todoDate instanceof TodoDateRep) {
                    todoDateRepository.deleteRep((TodoDateRep) todoDate);
                }
            }
            /*to-do 저장*/
            todoRepository.update(todoUpdateForm, todoId);
            /*todoDate 다시 만들기*/
            LocalDate startDate = LocalDate.now();
            LocalDate endDate = LocalDate.now();
            if (plan.getDtype().equals("Term")) {
                PlanTerm planTerm = (PlanTerm) plan;
                endDate = planTerm.getEndDate();
            }
            System.out.println("startDate = " + startDate);
            System.out.println("endDate = " + endDate);

            Todo newTodo = todoRepository.findOne(todoId);
            todoDateService.todoDateInitiate(startDate, endDate, newTodo);
        } else {
            /*repOption도 repValue도 바뀌지 않은 경우(타이틀만 변경)*/
            /*to-do 업데이트*/
            todoRepository.update(todoUpdateForm, todoId);
            Todo newTodo = todoRepository.findOne(todoId);
            /*todoDate 업데이트 (타이틀 변경)*/
            List<TodoDate> todoDates = todoRepository.getTodoDateRepByTodoId(todoId);
            for (TodoDate todoDate : todoDates) {
                if (todoDate instanceof TodoDateRep) {
                    todoDateRepository.updateRep(newTodo, todoDate.getId());
                }
            }
        }
    }

    public List<Todo> getTodoByPlanId(Long planId) {
        return todoRepository.getTodoByPlanId(planId);
    }

    public List<Todo> getTodoByDate(Plan plan, LocalDate date) {
        List<Todo> resultList = todoRepository.getTodoByPlanIdAndDate(plan, date);
        return getTodoByDate_Filter(resultList, date);
    }

    public List<Todo> getTodoByDate_Filter(List<Todo> resultList, LocalDate date) {
        if (resultList.isEmpty()) {
            return resultList;
        }
        List<Todo> filteredTodo = new ArrayList<>();
        for (Todo todo : resultList) {
            int repOption = todo.getRepOption();
            switch (repOption) {
                case 1:
                    List<String> repValue1 = todo.getRepValue();
                    String dayOfWeek = commonService.turnDayOfWeekToString(date.getDayOfWeek());

                    if (repValue1.contains(dayOfWeek)) {
                        filteredTodo.add(todo);
                    }
                    break;
                case 2:
                    int repValue2 = Integer.parseInt(todo.getRepValue().get(0));
                    LocalDate startDate = todo.getPlan().getStartDate();
                    int diffDays = Period.between(startDate, date).getDays();
                    if ((diffDays % repValue2) == 0) {
                        filteredTodo.add(todo);
                    }
                    break;
                default:
                    filteredTodo.add(todo);
            }
        }
        return filteredTodo;
    }

}