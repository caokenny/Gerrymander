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


// var statesArr = [
//     {name : "colorado", jsvar : colorado, precinctsVar: coloradogeo},
//     {name : "kansas", jsvar : kansas, precinctsVar: kansasgeo},
//     {name : "missouri", jsvar : missouri, precinctsVar: missourigeo}
// ];

var onStateAlready = false;

var stateNames = [
    {name : "colorado", jsvar: colorado},
    {name : "kansas", jsvar: kansas},
    {name : "missouri", jsvar: missouri}
];

var stateEvents = [];
var i;
for ( i = 0; i < stateNames.length; i++) {
    stateEvents[i] = L.geoJSON(stateNames[i].jsvar, {
            style: function () {
                return {color: "black", opacity: 1, fillColor: "orange", fillOpacity: 1}
            },
            name: stateNames[i].name
        }).addTo(mymap);
}

for ( i = 0; i < stateNames.length; i++) {
    stateEvents[i].on('click', function () {
        console.log(this);
        zoomState(this.getBounds(), this, this.options.name);
    });
    stateEvents[i].on('mouseover', function () {
        if (!onStateAlready) {
            this.setStyle({
                fillColor: "gray"
            });
        }
    });
    stateEvents[i].on('mouseout', function () {
        if (!onStateAlready) {
            this.setStyle(initialStyle);
        }
    });
}

var precinctLayer;
var districtLayer;
// var color = {"1" :}
var zoomLevel;

function zoomState(bounds, geoObj, stateName) {
    if (!onStateAlready) {
        onStateAlready = true;
        $('#welcomeDiv').css("display", "none");
        $('#buttons').css("display", "flex");
        $('#measuresContainer').css("display", "flex");
        $('#summaryBox').css("display", "block");
        $('#usercontrol').css("backgroundColor", "black");
        $('#algorithmChoiceDiv').css("display", "flex");
        var j;
        for (j = 0; j < stateEvents.length; j++) {
            mymap.removeLayer(stateEvents[j]);
        }
        geoObj.addTo(mymap).setStyle(initialStyle);
        $.getJSON("/js/" + stateName + "_district.json", function (data) {
            districtLayer = L.geoJSON(data, {
                style: function () {
                    return {color: "black", opacity: 1};
                }
            }).addTo(mymap);
        });
        mymap.fitBounds(bounds);
        $.getJSON("/js/" + stateName + "_final.json", function (data) {
            precinctLayer = L.geoJSON(data, {
                style: function () {
                    return {color: "black", opacity: 1};
                }
            });
        });
        zoomLevel = mymap.getBoundsZoom(bounds);
        checkZoom();
    }
}

function checkZoom() {
    mymap.on('zoomend', function () {
        var currentZoomLevel = mymap.getZoom();
        if (currentZoomLevel > zoomLevel) {
            mymap.addLayer(precinctLayer);
        } else if (currentZoomLevel <= zoomLevel) {
            if (mymap.hasLayer(precinctLayer)) {
                mymap.removeLayer(precinctLayer);
            }
        }
    })
}



// function addPrecincts() {
//     console.log(geoJSONEvents[0].geo.getAttribution());
// }
//
$('#selectStateSubmit').click(function () {
    var selectMenu = document.getElementById("stateSelectMenu");
    var selected = selectMenu.options[selectMenu.selectedIndex].value;
    for (var i = 0; i < stateEvents.length; i++) {
        if (stateEvents[i].options.name === selected) {
            stateEvents[i].fire('click');
            break;
        }
    }
});

function goHome() {
    if (onStateAlready) {
        var j;
        for (j = 0; j < stateEvents.length; j++) {
            if (!mymap.hasLayer(stateEvents[j])) {
                stateEvents[j].addTo(mymap);
            }
        }
        mymap.removeLayer(precinctLayer);
        mymap.removeLayer(districtLayer);
    }
    onStateAlready = false;
    precinctLayer = null;
    districtLayer = null;
    mymap.setView([37.0902, -95.7129], 4);
    document.getElementById("buttons").style.display = "none";
    document.getElementById("measuresContainer").style.display = "none";
    document.getElementById("summaryBox").style.display = "none";
    document.getElementById("usercontrol").style.backgroundColor = "orange";
    document.getElementById("welcomeDiv").style.display = "block";
    document.getElementById("algorithmChoiceDiv").style.display = "none";
}

// $('#runButton').click(function () {
//     var s;
//     var req;
//     var a = $('#algorithmChoice').val();
//     var m1 = $('#compactnessSlider').val();
//     var m2 = $('#populationSlider').val();
//     var m3 = $('#partisanFairnessSlider').val();
//     var m4 = $('#efficiencyGapSlider').val();
//     console.log(a); // prints rg
//     console.log(m1); // prints value correctly
//     var algObj = {"state": s, "compactness": m1, "populationEquality": m2, "partisanFairness": m3, "efficencyGap": m4, "algorithm": a};
//     $.ajax({
//         type: "POST",
//         contentType: "application/json",
//         url: "/algorithm",
//         data: JSON.stringify(algObj),
//         dataType: 'json',
//         cache: false,
//         timeout: 600000,
//         success: function (data) {
//
//             var json = "<h4>Ajax Response</h4><pre>"
//                 + JSON.stringify(data, null, 4) + "</pre>";
//             $('#measuresContainer').html(json);
//
//             console.log("SUCCESS : ", data);
//         },
//         error: function (e) {
//             var json = "<h4>Ajax Response</h4><pre>"
//                 + e.responseText + "</pre>";
//             $('#measuresContainer').html(json);
//
//             console.log("ERROR : ", e);
//         }
//     });
//
// });
//
// function validateAlgorithmSuccess() {}
// if (req.readyState === 4 && req.status === 200) {
//     alert(req.responseText);
// }
