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

/*to-do 상태 바꾸기*/
function planSwitchTodoDateStatus(planId, todoDateId) {
    let uri = "/plan/todoDate/switching?planId=" + planId + "&todoDateId=" +todoDateId;
    let tagId = "#" + todoDateId;
    $.ajax({
        url: uri,
        type: "POST"
    }).done(function (fragment) {
        $('#body').replaceWith(fragment);
    });
}

function homeSwtichTodoDateStatus(selectedDate, todoDateId) {
    let uri = "/home/calendar/todoDate/switching?selectedDate=" + selectedDate + "&todoDateId=" + todoDateId;
    $.ajax({
        url: uri,
        type: "POST"
    }).done(function(fragment) {
        $('#dateBlock').replaceWith(fragment);
    })
}

function getButtonBlock(planId, todoId) {
    let uri = "/plan/todo/block?planId=" + planId + "&todoId=" + todoId;
    $.ajax({
        url: uri,
        type: "GET"
    }).done(function(fragment) {
        $('#ButtonBlock').replaceWith(fragment);
    })
}

function getTodoUpdateForm(planId, todoId) {
    let uri = "/plan/todo?planId=" + planId + "&todoId=" + todoId;
    $.ajax({
        url: uri,
        type: "GET"
    }).done(function(fragment) {
        $('#todoButtonBlock').replaceWith(fragment);
    })
}

function getTodoDateDetailBlock(selectedDate, tododateId) {
    let uri = "/plan/todoDate?todoDateId=" + tododateId + "&selectedDate=" + selectedDate;
    $.ajax({
        url: uri,
        type: "GET"
    }).done(function(fragment) {
        $('#detailBlock').empty().append(fragment);
        $('#comment-form-box').css("display", "inline");
    })
}


$('body')
    .on('click', '#write', function(event) {
    event.preventDefault();
    $.ajax({
        url: "/plan/todoDate/comment",
        type: "POST",
        data: $('#comment-form').serialize()
    }).done(function(fragment) {
        $('#detailBlock').empty().append(fragment);
        $('#comment').val("");
    })
})

function deleteComment(selectedDate, commentId, todoDateId) {
    let uri = "/plan/todoDate/comment?selectedDate="+selectedDate+"&commentId="+commentId+"&todoDateId="+todoDateId;
    $.ajax({
        url: uri,
        type: "DELETE"
    }).done(function(fragment) {
        $('#detailBlock').html(fragment);
    })
}