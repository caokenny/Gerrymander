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

$('input[type="range"]').on('input', function() {

    var control = $(this),
        controlMin = control.attr('min'),
        controlMax = control.attr('max'),
        controlVal = control.val(),
        controlThumbWidth = control.data('thumbwidth');

    var range = controlMax - controlMin;

    var position = ((controlVal - controlMin) / range) * 100;
    var positionOffset = Math.round(controlThumbWidth * position / 100) - (controlThumbWidth / 2);
    var output = control.next('output');

    output
        .css('left', 'calc(' + position + '% - ' + positionOffset + 'px)')
        .text(controlVal);

});












