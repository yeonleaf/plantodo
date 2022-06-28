package demo.plantodo.controller;

import demo.plantodo.domain.Member;
import demo.plantodo.domain.Plan;
import demo.plantodo.service.MemberService;
import demo.plantodo.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequestMapping("/sse")
@RestController
@RequiredArgsConstructor
public class SseController {
    private final MemberService memberService;

    private static final Map<Long, SseEmitter> clients = new ConcurrentHashMap<>();

    @GetMapping("/subscribe")
    public SseEmitter subscribe(HttpServletRequest request) throws IOException {
        Long memberId = memberService.getMemberId(request);

        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        clients.put(memberId, emitter);
        SseEmitter.SseEventBuilder initEvent = SseEmitter.event()
                .id(String.valueOf(memberId))
                .name("dummy")
                .data("EventStream Created. This is dummy data of [userId : " + memberId + "]");
        emitter.send(initEvent);
        emitter.onTimeout(() -> clients.remove(memberId));
        emitter.onCompletion(() -> clients.remove(memberId));
        return emitter;
    }

    @GetMapping("/sendAlarm")
    public void sendAlarm(HttpServletRequest request) {
        Long memberId = memberService.getMemberId(request);
        int deadline_alarm_term = memberService.findOne(memberId).getSettings().getDeadline_alarm_term();
        /*실험용 (나중에 new ArrayList<>() 차이에 Plan 리스트를 조회해서 넣어야 함*/
        Thread loginThread = new Thread(new SendAlarmRunnable(memberId, new ArrayList<>(), deadline_alarm_term));
        loginThread.setName("loginThread" + memberId);
        loginThread.start();
    }

    @GetMapping("/quitAlarm")
    public void quitAlarm(HttpServletRequest request) {
        Long memberId = memberService.getMemberId(request);
        Map<Thread, StackTraceElement[]> traces = Thread.getAllStackTraces();
        for (Thread thread : traces.keySet()) {
            if (thread.getName().equals("loginThread"+memberId)) {
                thread.interrupt();
                break;
            }
        }
    }

    class SendAlarmRunnable implements Runnable {
        private Long memberId;
        private List<Plan> data = new ArrayList();
        private int alarm_term;

        public SendAlarmRunnable(Long memberId, List<Plan> data, int alarm_term) {
            this.memberId = memberId;
            this.data = data;
            this.alarm_term = alarm_term;
        }

        @Override
        public void run() {
            while (!Thread.interrupted()) {
                if (clients.containsKey(memberId)) {
                    SseEmitter client = clients.get(memberId);
                    try {
                        /*data를 가공해서 alarm 전송*/
                        client.send("dummy data - 나중에 수정");
                        Thread.sleep(alarm_term*1000);
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                        break;
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                        break;
                    }
                } else {
                    break;
                }
            }
        }
    }
}
