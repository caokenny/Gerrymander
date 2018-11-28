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
    var xhr = new XMLHttpRequest();
    xhr.open("POST", "/register", false);
    xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");

    xhr.onreadystatechange = function() {
        if (xhr.responseText !== "Success") {
            alert(xhr.responseText);
        }
    };

    xhr.send(
        "username=" + $('#registerUsername').val() +
        "&password=" + $('#registerPassword').val() +
        "&verifypassword=" + $('#registerVerifyPassword').val() +
        "&email=" + $('#registerEmail').val() +
        "&admin=false");
});

$('.loginSubmit').click(function () {
    var xhr = new XMLHttpRequest();
    xhr.open("POST", "/login", false);
    xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");

    xhr.onreadystatechange = function () {
        if (xhr.responseText !== "Success") {
            alert(xhr.responseText);
        }
    };

    xhr.send(
        "username=" + $('#loginUsername').val() +
            "&password=" + $('#loginPassword').val()
    );
});

$('.logoutButton').click(function () {
    var xhr = new XMLHttpRequest();
    xhr.open("POST", "/logout", false);

    xhr.onreadystatechange = function() {
        $.get("/");
    };

    xhr.send();
});


var stompClient = null;
function connect() {
    var socket = new SockJS('/websocket-example');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log("Connected: " + frame);
        stompClient.subscribe("/index/change", function (color) {
            document.getElementById("usercontrol").style.backgroundColor = color.body;
            console.log(color.body);
        });
    });
}

$(function () {
    $('.connect').click( function () {
        connect();
    });
    $('.colorChange').click(function () {
        stompClient.send("/app/color");
    })
});



// $('.colorChange').click(function () {
//     var xhr = new XMLHttpRequest();
//     xhr.open("POST", "/index/color", true);
//     xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
//
//     xhr.onload = function () {
//         document.getElementById("usercontrol").style.backgroundColor = xhr.responseText;
//     };
//
//     // var formdata = new FormData();
//     // formdata.append("color", $(this).val());
//
//     xhr.send("color=" + $(this).val());
// });


// var color;
// $('#runButton').click(function () {
//     var xhr = new XMLHttpRequest();
//     xhr.open("POST", "/rg/pickrgseed", true);
//     xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
//
//     xhr.onload = function () {
//         //Change precinct colors here
//         var obj = JSON.parse(xhr.responseText);
//         for (var i = 0; i < obj.Colors.length; i++) {
//             color = obj.Colors[i];
//             // console.log(color.red);
//             // console.log(color.blue);
//             // console.log(color.green);
//             // console.log(color.precinctID);
//             var index;
//             for (var j = 0; j < geoJSONEvents.length; j++) {
//                 if (mymap.hasLayer(geoJSONEvents[j].geo)) {
//                     // geoJSONEvents[j].precincts.addTo(mymap);
//                     index = j;
//                     break;
//                 }
//             }
//             geoJSONEvents[index].precincts.setStyle(function (feature) {
//                 // if (feature.properties.GEOID10 === "290032") {
//                 //     return {fillColor : "purple", fillOpacity : 1}
//                 // }
//                 return {fillColor : "rgb(" + color.red + "," + color.green + "," + color.blue + ")", fillOpacity : 1};
//             });
//         }
//     };
//
//     xhr.send("seeds=3");
// });











