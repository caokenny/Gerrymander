var mymap = L.map('mapid', {zoomControl: false}).setView([37.0902, -95.7129], 4);
mymap.doubleClickZoom.disable();

var mapboxtile = L.tileLayer('https://api.mapbox.com/styles/v1/mapbox/emerald-v8/tiles/{z}/{x}/{y}?access_token={accessToken}', {
    attribution: 'Map data &copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors, <a href="https://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="https://www.mapbox.com/">Mapbox</a>',
    minZoom: 4,
    id: 'mapbox.streets',
    accessToken: 'pk.eyJ1IjoiY2Fva2VubnkiLCJhIjoiY2ptZHhzcmJoMHVlYjNwbW90cm1kZW11bSJ9.C6aOC-2bLmc9SIXXjI0tyQ'
}).addTo(mymap);

//Add geoJSON


var states = L.geoJSON(usageo, {
    style: function (feature) {
        return {color: "black", fillColor: "blue", fillOpacity: 1}
    }
}).addTo(mymap);

// var continents = L.geoJSON(continentsgeo, {
//     style: function (feature) {
//         if (feature.properties.CONTINENT !== "North America") {
//             return {color: "black", opacity: 1, fillColor: "black", fillOpacity: 1};
//         } else {
//             return {color: "black"};
//         }
//     }
// }).addTo(mymap);


var initialStyle = {color: "black", opacity: 1, fillColor: "orange", fillOpacity: 1};


var statesArr = [
    {name : "colorado", jsvar : colorado, precinctsVar: coloradogeo},
    {name : "kansas", jsvar : kansas, precinctsVar: kansasgeo},
    {name : "missouri", jsvar : missouri, precinctsVar: missourigeo}
];


var geoJSONEvents = [];

var i;
for (i = 0; i < statesArr.length; i++) {
    geoJSONEvents[i] = {stateName : statesArr[i].name, geo : L.geoJSON(statesArr[i].jsvar, {
        style: function () {
            return {color: "black", opacity: 1, fillColor: "orange", fillOpacity: 1}
        }
    }).addTo(mymap), precincts : L.geoJSON(statesArr[i].precinctsVar)};
}

var onStateAlready = false;

for (i = 0; i < geoJSONEvents.length; i++) {
    geoJSONEvents[i].geo.on('click', function () {
        zoomState(this.getBounds(), this);
    });

    geoJSONEvents[i].geo.on('mouseover', function () {
        if (!onStateAlready) {
            this.setStyle({
                color: "white"
            });
        }
    });

    geoJSONEvents[i].geo.on('mouseout', function () {
        if (!onStateAlready) {
            this.setStyle(initialStyle);
        }
    });
}

function addPrecincts() {
    console.log(geoJSONEvents[0].geo.getAttribution());
}

$('#selectStateSubmit').click(function () {
    var selectMenu = document.getElementById("stateSelectMenu");
    var selected = selectMenu.options[selectMenu.selectedIndex].value;
    var i;
    for (i = 0; i < geoJSONEvents.length; i++) {
        if (geoJSONEvents[i].stateName === selected) {
            geoJSONEvents[i].geo.fire('click');
            break;
        }
    }
});

function zoomState(bounds, geoObj) {
    if (!onStateAlready) {
        onStateAlready = true;
        document.getElementById("welcomeDiv").style.display = "none";
        document.getElementById("buttons").style.display = "flex";
        document.getElementById("measuresContainer").style.display = "flex";
        document.getElementById("summaryBox").style.display = "block";
        document.getElementById("usercontrol").style.backgroundColor = "black";
        document.getElementById("algorithmChoiceDiv").style.display = "flex";
        // var j;
        // for (j = 0; j < geoJSONEvents.length; j++) {
        //     geoJSONEvents[j].geo.setStyle({fillColor: "blue", fillOpacity: 1});
        // }
        // geoObj.setStyle(initialStyle);
        var j;
        for (j = 0; j < geoJSONEvents.length; j++) {
            mymap.removeLayer(geoJSONEvents[j].geo);
        }
        geoObj.addTo(mymap);
        for (j = 0; j < geoJSONEvents.length; j++) {
            if (mymap.hasLayer(geoJSONEvents[j].geo)) {
                geoJSONEvents[j].precincts.addTo(mymap);
            }
        }
        mymap.fitBounds(bounds);
    }
}

function goHome() {
    onStateAlready = false;
    var j;
    for (j = 0; j < geoJSONEvents.length; j++) {
        if (!mymap.hasLayer(geoJSONEvents[j].geo)) {
            geoJSONEvents[j].geo.addTo(mymap);
        }
        if (mymap.hasLayer(geoJSONEvents[j].precincts)) {
            mymap.removeLayer(geoJSONEvents[j].precincts);
        }
    }
    mymap.setView([37.0902, -95.7129], 4);
    document.getElementById("buttons").style.display = "none";
    document.getElementById("measuresContainer").style.display = "none";
    document.getElementById("summaryBox").style.display = "none";
    document.getElementById("usercontrol").style.backgroundColor = "orange";
    document.getElementById("welcomeDiv").style.display = "block";
    document.getElementById("algorithmChoiceDiv").style.display = "none";
}

$('#runButton').click(function () {
    var s;
    var req;
    var a = $('#algorithmChoice').val();
    var m1 = $('#compactnessSlider').val();
    var m2 = $('#populationSlider').val();
    var m3 = $('#partisanFairnessSlider').val();
    var m4 = $('#efficiencyGapSlider').val();
    console.log(a); // prints rg
    console.log(m1); // prints value correctly
    var algObj = {"state": s, "compactness": m1, "populationEquality": m2, "partisanFairness": m3, "efficencyGap": m4, "algorithm": a};
    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: "/algorithm",
        data: JSON.stringify(algObj),
        dataType: 'json',
        cache: false,
        timeout: 600000,
        success: function (data) {

            var json = "<h4>Ajax Response</h4><pre>"
                + JSON.stringify(data, null, 4) + "</pre>";
            $('#measuresContainer').html(json);

            console.log("SUCCESS : ", data);
        },
        error: function (e) {
            var json = "<h4>Ajax Response</h4><pre>"
                + e.responseText + "</pre>";
            $('#measuresContainer').html(json);

            console.log("ERROR : ", e);
        }
    });

});

function validateAlgorithmSuccess() {}
if (req.readyState === 4 && req.status === 200) {
    alert(req.responseText);
}

// L.geoJSON(kansasgeo, {
//    style: function (feature) {
//        if (feature.properties.KS_GEO_ID === 13) {
//            return {fillColor: "red", fillOpacity: 1};
//        }
//        if (feature.properties.KS_GEO_ID === 1) {
//            return {fillColor: "black", fillOpacity: 1};
//        }
//        if (feature.properties.KS_GEO_ID === 27) {
//            return {fillColor: "green", fillOpacity: 1};
//        }
//        if (feature.properties.KS_GEO_ID === 9) {
//            return {fillColor: "purple", fillOpacity: 1};
//        }
//        if (feature.properties.KS_GEO_ID === 16) {
//            return {fillColor: "yellow", fillOpacity: 1};
//        }
//        if (feature.properties.KS_GEO_ID === 17) {
//            return {fillColor: "white", fillOpacity: 1};
//        }
//    }
// }).addTo(mymap);