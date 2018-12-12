var loginOpen = false;
var registerOpen = false;

$('#loginButton').on('click', function () {
    if (registerOpen) {
        closeRegister();
    }
    loginOpen = true;
    $('#loginDiv').css("display", "block");
});

function closeLogin() {
    loginOpen = false;
    $('#loginDiv').css("display", "none");
}

$('#registerButton').on('click', function () {
    if (loginOpen) {
        closeLogin();
    }
    registerOpen = true;
    $('#registerDiv').css("display", "block");
});

function closeRegister() {
    registerOpen = false;
    $('#registerDiv').css("display", "none");
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

$('#openSideNav').on('click', function () {
    if ($('#usercontrol').is(':visible')) {
        $('#mySidenav').css("width", "400px");
    } else {
        $('#mySidenav').css("width", "250px");
    }
    $('#openSideNav').css("display", "none");
});

$('.closebtn').on('click', function () {
    $('#mySidenav').css("width", "0");
    $('#openSideNav').css("display", "block");
});

$('#redistrictBtn').on('click', function () {
    if ($('#stateDropdownContent').is(':visible')) {
        $('#stateDropdownContent').css("display", "none");
        // $('#otherSideNavLinks').css("padding-top", "0");
        // $('.userLog').css("padding-top", "0");
        // $('#adminSettings').css("padding-top", "0");
    } else {
        $('#stateDropdownContent').css("display", "block");
        // $('.userLog').css("padding-top", "200px");
        // $('#adminSettings').css("padding-top", "200px");
        // $('#otherSideNavLinks').css("padding-top", "200px");
    }
});












