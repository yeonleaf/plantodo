/*일자별 Plan, To-do 조회*/
function loadDateBlockData(searchDate) {
    let uri = "/home/calendar/" + searchDate;
    $.ajax({
        url: uri,
        type: "GET"
    }).done(function (fragment) {
        $("#dateBlock").replaceWith(fragment);
    });
}

/*plan*/
function planDetailAjax(planId) {
    $.ajax({
        url: "/plan/" + planId,
        type: "GET"
    }).done( function (fragment) {
        $('body').empty().html(fragment);
    })
}

function switchPlanEmphasis(planId, pageInfo) {
    let uri = "/plan/emphasizing?planId="+planId+"&pageInfo="+pageInfo ;
    $.ajax({
        url: uri,
        type: "POST"
    })
}

/*to-do 상태 바꾸기*/
function switchTodoDateStatus_home(todoDateId) {
    let uri = "/todoDate/switching?todoDateId="+todoDateId;
    $.ajax({
        url: uri,
        type: "POST"
    })
}

function switchTodoDateStatus_detail(todoDateId) {
    let uri = "/todoDate/switching?todoDateId="+todoDateId;
    $.ajax({
        url: uri,
        type: "POST"
    }).done(function (data) {
        console.log(data);
        $("#progress_bar").css("width", data+"%");
        $("#progress_bar").data("aria-valuenow", data);
        $("#progress_bar").text(data + "%");
    })

}

function getTodoButtonBlock(planId, todoId) {
    let uri = "/todo/block?planId=" + planId + "&todoId=" + todoId;

    let state = $('#' + todoId + 'ButtonBlock').data('state');
    if (state === undefined) {
        $.ajax({
            url: uri,
            type: "GET"
        }).done(function(fragment) {
            $('#' + todoId + 'ButtonBlock').data("state", "clicked");
            $('#'+todoId+'ButtonBlock').empty().append(fragment);
        })
    } else {
        $('#' + todoId + 'ButtonBlock').empty();
        $('#' + todoId + 'ButtonBlock').removeData("state");
    }

}

function getTodoDateRegisterForm(planId) {
    if ($("#dailyTdRegister"+planId).css("display") === "none") {
        $("#dailyTdRegister" + planId).css("display", "inline");
    } else {
        $("#dailyTdRegister" + planId).css("display", "none");
    }

}

function deleteTodoDate(todoDateId) {
    let form = $('#deleteForm'+todoDateId).serialize();
    $.ajax({
        url: "/todoDate",
        type: "DELETE",
        data: form,
        success: function (res) {
            if (res.pageInfo == "home") {
                loadDateBlockData(res.searchDate);
            } else {
                planDetailAjax(res.planId)
            }
        }
    })
}


// tododate

function registerTodoDateDaily(planId) {
    let form = $('#dailyTdRegisterForm'+planId).serialize();

    $.ajax({
        url: "/todoDate/daily",
        type: "POST",
        data: form,
        success: function(res) {
            let searchDate = res.searchDate;
            loadDateBlockData(searchDate);
        }
    });
}

function getTodoUpdateForm(planId, todoId) {
    let uri = "/todo?planId=" + planId + "&todoId=" + todoId;
    $.ajax({
        url: uri,
        type: "GET"
    }).done(function(fragment) {
        $('#todoButtonBlock').replaceWith(fragment);
    })
}


function getTodoDateEditForm(pageInfo, selectedDate, planId, todoDateId, todoDateTitle) {
    let div_row = document.createElement("div");
    div_row.className = "row mx-1 my-1"

    // edit input
    let div_col1 = document.createElement("div");
    div_col1.className = "col"

    let input = document.createElement("input");
    input.id = "editTitle";
    input.name = "editTitle";
    input.setAttribute("pageInfo", pageInfo);
    input.setAttribute("selectedDate", selectedDate);
    input.setAttribute("planId", planId);
    input.setAttribute("todoDateId", todoDateId);
    input.className = "form-control";
    input.value = todoDateTitle;

    div_col1.appendChild(input);

    // edit button
    let div_col2 = document.createElement("div");
    div_col2.className = "col"

    let btn = document.createElement("input");
    btn.type = "button"
    btn.id = "editBtn"
    btn.name = "editBtn"
    btn.value = "EDT"
    btn.className = "btn btn-sm btn-primary"
    btn.onclick = function() {
        let pageInfo = $('#editTitle').attr("pageInfo");
        let selectedDate = $('#editTitle').attr("selectedDate");
        let planId = $('#editTitle').attr("planId");
        let todoDateId = $('#editTitle').attr("todoDateId");

        let data = { pageInfo: pageInfo,
            selectedDate: selectedDate,
            planId: planId,
            todoDateId: todoDateId,
            updateTitle: $('#editTitle').val() }

        console.log(data);

        $.ajax({
            url: "/todoDate",
            type: "PUT",
            data: data,
            success: function (res) {
                setTimeout(function () {
                    console.log(res.pageInfo);
                    if (res.pageInfo == "home") {
                        loadDateBlockData(res.searchDate);
                    } else {
                        planDetailAjax(planId);
                    }
                }, 100);
            }
        })
    }
    div_col2.appendChild(btn);

    div_row.appendChild(input);
    div_row.appendChild(btn);

    $("#" + todoDateId).empty().html(div_row);
}

// comment

$(document).on("click", "#blockTrigger", function(event) {
    console.log("*");
    event.preventDefault();
    let selectedDate = $(this).attr("selectedDate");
    let tododateId = $(this).attr("todoDateId");
    let state = $('#detailBlock' + tododateId).data("state");
    if (state === undefined) {
        let uri = "/todoDate?todoDateId=" + tododateId + "&selectedDate=" + selectedDate;
        $.ajax({
            url: uri,
            type: "GET"
        }).done(function(fragment) {
            $('#detailBlock'+tododateId).data("state", "clicked");
            $('#detailBlock'+tododateId).empty().append(fragment);
        })
    } else {
        $('#detailBlock'+tododateId).empty()
        $('#detailBlock'+tododateId).removeData("state");
    }
});


function registerComment(selectedDate, todoDateId) {

    let data = {
        'selectedDate': selectedDate,
        'todoDateId': todoDateId,
        'comment': $('#comment-input').val()
    }

    $.ajax({
        url: "/comment",
        type: "POST",
        data: data
    }).done(function(fragment) {
        $('#detailBlock' + todoDateId).empty().append(fragment);
        $('#comment-input').val("");
    })
}


function deleteComment(selectedDate, commentId, todoDateId) {
    let uri = "/comment?selectedDate="+selectedDate+"&commentId="+commentId+"&todoDateId="+todoDateId;
    $.ajax({
        url: uri,
        type: "DELETE"
    }).done(function(fragment) {
        $('#detailBlock'+todoDateId).html(fragment);
    })
}

function getCommentUpdateForm(selectedDate, todoDateId, commentId) {
    let input = document.createElement("input");
    input.id = "commentUpdateInput"
    input.setAttribute("selectedDate", selectedDate);
    input.setAttribute("todoDateId", todoDateId);
    input.setAttribute("commentId", commentId);
    input.className = "form-control"
    input.value = $('#'+commentId+"title").text();

    $('#'+commentId+"title").html(input);
    $('#' + commentId + "delbtn").css("display", "none");
    $('#' + commentId + "edtBefore").css("display", "none");
    $('#' + commentId + "edtAfter").css("display", "inline");

}


function editComment() {
    let commentId = $("#commentUpdateInput").attr("commentId");
    let updatedComment = $("#commentUpdateInput").val();
    $.ajax({
        url: "/comment?commentId="+commentId+"&updatedComment="+updatedComment,
        type: "PUT",
        success: function() {
            let span = document.createElement("span");
            span.id = commentId + "title";
            span.innerText = updatedComment;
            span.value = commentId;

            $("#"+commentId+"title").empty().html(span);
            $("#"+commentId+"editBtn").empty();
            $("#"+commentId+"delbtn").css("display", "inline");
            $("#"+commentId+"edtbtn").css("display", "inline");
        },
        error: function(err) {
            alert(err);
        }
    })
}


/*settings*/
/*termBlock style을 none <- -> inline으로 변경한다.*/

$(document).on("click", "#deadline_alarm", function(event) {
    event.preventDefault();

    if ($("#termBlock").css("display") === "none") {
        $("#termBlock").css("display", "inline");
    } else {
        /*deadline_alarm을 false로 만들고 deadline_alarm_term을 0으로 초기화한다.*/
        fetch("/settings", {
            method: 'post',
            body: JSON.stringify({
                'settings_id': $('#settings_id').val(),
                'notification_perm': $('#notification_perm').val(),
                'deadline_alarm': false,
                'deadline_alarm_term': 0
            }),
            headers: {"Content-Type": "application/json"}
        }).then(function() {
            $("#termBlock").css("display", "none");
            $("#deadline_alarm_term").val(0);
        })
    }
})


/*submit 버튼을 클릭했을 때(무조건 off->on 상태) notification perm을 얻은 상태인지 얻지 않았는지 확인한다.*/
$(document).on("click", "#deadline_alarm_submit", function(event) {

    event.preventDefault();

    let notification_perm = $('#notification_perm').val();

    let data = JSON.stringify({
        settings_id: $('#settings_id').val(),
        notification_perm: "GRANTED",
        deadline_alarm: true,
        deadline_alarm_term: $('#deadline_alarm_term').val()
    });

    if (notification_perm === "GRANTED") {
        /*granted 상태이면 Notification 객체의 상태를 granted로 변경한다.*/
        if ('permission' in Notification) {
            Notification.permission = 'granted';
        }
        /*폼 정보를 update한다.*/
        fetch("/settings", {
            method: "POST",
            body: data,
            headers: {"Content-Type": "application/json"}
        });
    } else if (notification_perm === "DENIED" || Notification.permission === "default") {
        /*denied 상태이면 permission를 얻는 시도를 한다.*/
        Notification.requestPermission().then((permission) => {
            handlePermission(data, permission);
        })
    }

})

function handlePermission(data, permission) {
    /*permission이 granted상태가 되었다면*/
    if (permission === 'granted') {
        /*Notification.permission을 granted로 바꾼다.*/
        if ('permission' in Notification) {
            Notification.permission = 'granted';
        }
        /*form 데이터를 update한다.*/
        fetch("/settings", {
            method: "POST",
            body: data,
            headers: {"Content-Type": "application/json"}
        });
    } else {
        /*permission이 여전히 denied상태일 경우 데이터 update를 하지 않는다.*/
        if ('permission' in Notification) {
            Notification.permission = 'default';
        }
    }

}