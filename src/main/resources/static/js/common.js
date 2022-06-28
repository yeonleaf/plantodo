
function isCookieExists(name) {
    name = name + "=";
    let cookies = document.cookie;
    let start = cookies.indexOf(name);
    return false ? start == -1 : true;
}

function deleteCookie(name) {
    document.cookie = name + "=; expires=Thu, 01 Jan 1970 00:00:01 GMT;";
}