var loginOpen = false;
var registerOpen = false;
var aboutOpen = false;
$('#loginButton').on('click', function () {
    if (registerOpen) {
        closeRegister();
    }
    loginOpen = true;
    $('#loginDiv').css("display", "block");
});
$('#aboutButton').on('click', function () {
    aboutOpen = true;
    $('#aboutDiv').css("display", "block");
});
function closeAbout() {
    aboutOpen = false;
    $('#aboutDiv').css("display", "none");
}
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

function closeLoad() {
    $('#loadWeightsDiv').css("display", "none");
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
        $('#mySidenav').css("width", "350px");
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


$('#saveWeights').on('click', function () {
    var compactness = $('#compactnessSlider').val();
    var pf = $('#partisanFairnessSlider').val();
    var population = $('#populationSlider').val();
    var eg = $('#efficiencyGapSlider').val();
    $.ajax({
        url: "/user/saveweights",
        async: true,
        type: "POST",
        data: {"compactness": compactness, "population" : population, "pf" : pf, "eg": eg},
        success: function (data) {
            alert(data);
        }
    });
});

$('#loadWeights').on('click', function () {
    var loadDiv = $('#loadWeightsDiv');
    loadDiv.empty();
    loadDiv.css("display", "block");
    loadDiv.append("<span class=\"closeLoginBox\" onclick=\"closeLoad()\">&times;</span>");
    $.ajax({
        url: "/user/getsavedweights",
        async: true,
        type: "GET",
        success: function (data) {
            var jsonObj = JSON.parse(data);
            for (var i = 0; i < jsonObj.length; i++) {
                loadDiv.append("<p></p><a href='#' name='" + jsonObj[i].id + "' onclick='loadFile(name)'>" + (i+1) + ". " + "Compactness: " + jsonObj[i].compactness + " Population: " + jsonObj[i].population + " Partisan Fairness: " + jsonObj[i].pf + " Efficiency Gap: " + jsonObj[i].eg + "</a>");
            }
        }
    });
});

function loadFile(id) {
    $.ajax({
        url: "/user/loadweights",
        async: true,
        type: "POST",
        data: {"id" : id},
        success: function (data) {
            var jsonObj = JSON.parse(data);
            $('#compactnessSlider').val(jsonObj.compactness);
            $('#populationSlider').val(jsonObj.population);
            $('#partisanFairnessSlider').val(jsonObj.pf);
            $('#efficiencyGapSlider').val(jsonObj.eg);
        }
    })
}










