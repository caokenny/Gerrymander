var loginOpen = false;
var registerOpen = false;

function popupLogin() {
    if (registerOpen === true) {
        closeRegister();
        registerOpen = false;
    }
    loginOpen = true;
    var loginBox = document.getElementById("loginDiv");
    loginBox.style.display = "block";
}

function closeLogin() {
    loginOpen = false;
    var loginBox = document.getElementById("loginDiv");
    loginBox.style.display = "none";
}

function popupRegister() {
    if (loginOpen === true) {
        closeLogin();
        loginOpen = false;
    }
    registerOpen = true;
    var registerBox = document.getElementById("registerDiv");
    registerBox.style.display = "block";
}

function closeRegister() {
    registerOpen = false;
    var registerBox = document.getElementById("registerDiv");
    registerBox.style.display = "none";
}