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

$('.registerSubmit').click(function () {
    var username = $("#registerUsername").val();
    var password = $("#registerPassword").val();
    var email = $("#registerEmail").val();
    $.post("/register", {username : username, password: password, email : email}, function() {
        alert("Successful");
    })
});

// $('.loginSubmit').click(function () {
//     var username = $("#loginUsername").val();
//     var password = $("#loginPassword").val();
//     $.post("/login", {username : username, password : password}, function () {
//         alert("Successful");
//     })
// });


$('.loginSubmit').click(function () {
    var xhr = new XMLHttpRequest();
    xhr.open("POST", '/login', false);

    var formData = new FormData(document.getElementById("logincontent"));
    xhr.send(formData);
});

$('.colorChange').click(function () {
    var xhr = new XMLHttpRequest();
    xhr.open("POST", "/color", true);

    xhr.onload = function () {
        document.getElementById("usercontrol").style.backgroundColor = xhr.responseText;
    };

    var formdata = new FormData();
    formdata.append("color", $(this).val());

    xhr.send(formdata);
});











