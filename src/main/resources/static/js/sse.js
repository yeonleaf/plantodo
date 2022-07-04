/*로그인 후 첫 home 접속 (alarmStart cookie가 있어야 함)*/
if (sessionStorage.getItem("deadline_alarm_term") === null) {
    /*sessionStroage에 deadline_alarm_term 정보 저장*/
    sessionStorage.setItem("deadline_alarm_term", $("#deadline_alarm_term").val());

    /*subscribe*/
    let uri = "/sse/subscribe";
    const eventSource_start = new EventSource(uri);

    /*sendAlarm*/
    fetch(`/sse/sendAlarm`);

    eventSource_start.onopen = (e) => {
        console.log(e);
    }

    eventSource_start.onerror = (e) => {
        if (e.currentTarget.readyState == EventSource.CLOSED) {
        } else {
            eventSource.close();
        }
    }

    eventSource_start.onmessage = (e) => {
        let data = JSON.parse(e.data);
        let msg = "아직 완료되지 않은 일정이 " + data.count + "개 있습니다. 가장 마감이 임박한 일정으로 이동하시겠습니까?";
        let notification = new Notification('현재 메세지', {title: "마감 알림", body: msg});
        notification.actions = [
            {
                action: 'show-uncompleted-action',
                title: 'Message'
            }
        ]
        notification.onclick = function (e) {
            e.preventDefault();
            window.open("/plan/" + data.planId, '_blank');
        }
        setTimeout(notification.close.bind(notification), 5000);

        sessionStorage.setItem("msgLastSentTime", new Date().getTime());
        console.log(sessionStorage.getItem("msgLastSentTime"))
    }
} else {
    let msgLastSentTime = sessionStorage.getItem("msgLastSentTime");

    if (msgLastSentTime != null) {
        let deadline_alarm_term = sessionStorage.getItem("deadline_alarm_term");
        let waitTime = (new Date().getTime() - msgLastSentTime) % deadline_alarm_term;

        /*waitTime만큼 기다리기*/
        setTimeout(function () {
            /*subscribe*/
            let uri = "/sse/subscribe";
            const eventSource_dur = new EventSource(uri);

            /*sendAlarm*/
            fetch(`/sse/sendAlarm`);

            /*eventSource onopen, onerror, onmessage*/
            eventSource_dur.onopen = (e) => {
                console.log(e);
            }

            eventSource_dur.onerror = (e) => {
                if (e.currentTarget.readyState == EventSource.CLOSED) {
                } else {
                    eventSource.close();
                }
            }

            eventSource_dur.onmessage = (e) => {
                let data = JSON.parse(e.data);
                let msg = "아직 완료되지 않은 일정이 " + data.count + "개 있습니다. 가장 마감이 임박한 일정으로 이동하시겠습니까?";
                let notification = new Notification('현재 메세지', {title: "마감 알림", body: msg});
                notification.actions = [
                    {
                        action: 'show-uncompleted-action',
                        title: 'Message'
                    }
                ]
                notification.onclick = function (e) {
                    e.preventDefault();
                    window.open("/plan/" + data.planId, '_blank');
                }
                setTimeout(notification.close.bind(notification), 5000);

                sessionStorage.setItem("msgLastSentTime", new Date().getTime());
                console.log(sessionStorage.getItem("msgLastSentTime"))
            }
        }, waitTime * 1000);
    }
}
