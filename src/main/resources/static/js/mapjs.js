var mymap = L.map('mapid', {zoomControl: false}).setView([37.0902, -95.7129], 4);
mymap.doubleClickZoom.disable();

var mapboxtile = L.tileLayer('https://api.mapbox.com/styles/v1/mapbox/emerald-v8/tiles/{z}/{x}/{y}?access_token={accessToken}', {
    attribution: 'Map data &copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors, <a href="https://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="https://www.mapbox.com/">Mapbox</a>',
    minZoom: 4,
    id: 'mapbox.streets',
    accessToken: 'pk.eyJ1IjoiY2Fva2VubnkiLCJhIjoiY2ptZHhzcmJoMHVlYjNwbW90cm1kZW11bSJ9.C6aOC-2bLmc9SIXXjI0tyQ'
}).addTo(mymap);

var colors = {"01" : "red", "02" : "green", "03" : "purple", "04" : "#489ec9", "05" : "orange", "06" : "pink", "07" : "gray", "08" : "brown"};

//Add geoJSON

// L.geoJSON(usageo, {
//     style: function (feature) {
//         if (feature.properties.name !== "Colorado" && feature.properties.name !== "Kansas" && feature.properties.name !== "Missouri") {
//             return {color: "black", fillColor: "blue", fillOpacity: 1}
//         }
//     }
// }).addTo(mymap);

var initialStyle = {color: "white", opacity: 1, fillColor: "#5FBD5A", fillOpacity: 0.2};

var onStateAlready = false;

var stateNames = [
    {name : "colorado", jsvar: colorado, abbr: "CO"},
    {name : "kansas", jsvar: kansas, abbr: "KS"},
    {name : "missouri", jsvar: missouri, abbr: "MO"}
];

var stateEvents = [];
var i;
for ( i = 0; i < stateNames.length; i++) {
    stateEvents[i] = L.geoJSON(stateNames[i].jsvar, {
            style: function () {
                return {color: "white", opacity: 1, fillColor: "#5FBD5A", fillOpacity: 0.2}
            },
            name: stateNames[i].name, abbr: stateNames[i].abbr
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
var stateLayer;
var zoomLevel;
var stateSelected;

function zoomState(bounds, geoObj, stateName) {
    stateSelected = geoObj.options.abbr;
    if (!onStateAlready) {
        onStateAlready = true;
        $('#mySidenav').css("width", "400px");
        $('#stateDropdown').css("display", "none");
        $('#otherSideNavLinks').css("display", "none");
        $('.userLog').css("display", "none");
        $('#adminSettings').css("display", "none");
        $('#usercontrol').css("display", "flex");
        $('.sidenav a').css("font-size", "20px");
        $('.sidenav div').css("font-size", "20px");
        var j;
        for (j = 0; j < stateEvents.length; j++) {
            // mymap.removeLayer(stateEvents[j]);
            if (stateEvents[j] !== geoObj) {
                stateEvents[j].setStyle(function () {
                    return {opacity: 0, fillOpacity: 0};
                });
            }
        }
        stateLayer = geoObj;

        // geoObj.addTo(mymap).setStyle(initialStyle);
        $.getJSON("/js/json/" + stateName + "_district.json", function (data) {
            districtLayer = L.geoJSON(data, {
                style: function (feature) {
                    return {fillColor: colors[feature.properties.DISTRICT], fillOpacity: 0.2, color: "white", opacity: 1};
                }
            }).addTo(mymap);
        });
        zoomLevel = mymap.getBoundsZoom(bounds) - 1;
        // mymap.setZoom(zoomLevel);
        mymap.setView(bounds.getCenter(), zoomLevel);
        // mymap.fitBounds(bounds);
        $.getJSON("/js/json/" + stateName + "_final.json", function (data) {
            precinctLayer = L.geoJSON(data, {
                style: function (feature) {
                    return {color: "#DCDCDC", opacity: 0.5, fillColor: colors[feature.properties.DISTRICT], fillOpacity: 1};
                },
                onEachFeature: function(feature, layer) {
                    layer.on('mouseover', function () {
                        $('#infoPopup').css("display", "block");
                        $('#geoid').text("GEOID: " + feature.properties.GEOID10);
                        $('#populationInfo').text("Population: " + feature.properties.POP100);
                        $('#presidentialD').text("Democrat: " + parseInt(feature.properties.PRES_D_08));
                        $('#presidentialR').text("Republican: " + parseInt(feature.properties.PRES_R_08));4
                    });
                    layer.on('mouseout', function () {
                        $('#infoPopup').css("display", "none");
                    });
                },
                name: stateName,
                abbr: geoObj.abbr
            })
        });
        checkZoom();
    }

}

function checkZoom() {
    // mymap.setView()
    mymap.on('zoomend', function () {
        var currentZoomLevel = mymap.getZoom();
        if (currentZoomLevel > zoomLevel) {
            mymap.addLayer(precinctLayer);
            mymap.removeLayer(districtLayer);
        } else if (currentZoomLevel <= zoomLevel) {
            if (mymap.hasLayer(precinctLayer)) {
                mymap.removeLayer(precinctLayer);
                mymap.addLayer(districtLayer);
                $('#infoPopup').css("display", "none");
            }
        }
    })
}


$('.stateSelect').on('click', function () {
    var id = $(this).attr('id');
    for (var i = 0; i < stateEvents.length; i++) {
        if (stateEvents[i].options.name === id) {
            stateEvents[i].fire('click');
            break;
        }
    }
});

$('.homeBtn').on('click', function () {
    if (onStateAlready) {
        var j;
        for (j = 0; j < stateEvents.length; j++) {
            stateEvents[j].setStyle(initialStyle);
        }
        mymap.removeLayer(precinctLayer);
        mymap.removeLayer(districtLayer);
    }
    onStateAlready = false;
    precinctLayer = null;
    districtLayer = null;
    mymap.setView([37.0902, -95.7129], 4);
    $('#mySidenav').css("width", "250px");
    $('#stateDropdown').css("display", "block");
    $('#otherSideNavLinks').css("display", "block");
    $('.userLog').css("display", "block");
    $('#adminSettings').css("display", "block");
    $('#usercontrol').css("display", "none");
    $('.sidenav a').css("font-size", "25px");
    $('.sidenav div').css("font-size", "25px");
});

var seed;
$('#updateButton').on('click', function () {
    $.ajax({
        url: "/rg/pickrgseed",
        async: true,
        // dataType: "json",
        type: "POST",
        // contentType: "application/json",
        data: {"stateName" : stateSelected, "seedNum" : 5},
        success: function (response) {
            precinctLayer.setStyle(function () {
                return {fillColor: "white"};
            });
            var jsonStr = JSON.parse(response);
            console.log(response);
            for (var i = 0; i < jsonStr.seeds.length; i++) {
                seed = jsonStr.seeds[i];
                console.log(seed);
                precinctLayer.setStyle(function (feature) {
                    if (feature.properties.GEOID10 === seed.precinctGeoId) {
                        return {fillColor: colors["0" + seed.districtID]};
                    }
                });
            }

        }
    })
});

var paused = false;
var summaryBox = $('#summaryBox');
$('#runButton').on('click', function () {
    $('#pauseButton').css("display", "block");
    $('#stopButton').css("display", "block");
    $(this).css("display", "none");
    paused = false;
    var algorithmChoice = $('#algorithmChoice').val();
    var compactness = $('#compactnessSlider').val();
    var population = $('#populationSlider').val();
    var partisanFariness = $('#partisanFairnessSlider').val();
    var efficencyGap = $('#efficiencyGapSlider').val();
    var measuresObj = {"compactness" : compactness, "populationEquality" : population, "partisanFairness" : partisanFariness, "efficencyGap" : efficencyGap, "algorithm" : algorithmChoice, "stateAbbrv" : stateSelected};
    if (algorithmChoice === "sa") {
        $.ajax({
            url: "setWeights",
            type: "POST",
            async: true,
            dataType: "json",
            contentType: "application/json",
            data: JSON.stringify(measuresObj),
            success: function (data) {
                console.log(data);
                continueAlgorithm()
            },
            error: function (e) {
                var json = "Ajax Response" + e.responseText;
                summaryBox.val(summaryBox.val() + json);
                console.log("ERROR : ", e);
            }
        })
    } else {
        $.ajax({
            url: "/rg/do10Rg",
            type: "POST",
            async: true,
            dataType: "json",
            contentType: "application/json",
            data: JSON.stringify(measuresObj),
            success: function (data) {
                if (data.updates.length !== 0) {
                    updatePrecinctVisual(data);
                }
            }
        })
    }
});

var precinctMove;
function updatePrecinctVisual(response) {
    for (var i = 0; i < response.updates.length; i++) {
        precinctMove = response.updates[i];
        precinctLayer.setStyle(function (feature) {
            if (feature.properties.GEOID10 === precinctMove.precinctId) {
                return {fillColor : colors["0" + precinctMove.destDistId], fillOpacity : 1};
            }
        });
    }
    // if (!paused) {
    //     $('#runButton').trigger('click');
    // }
}


$('#pauseButton').on('click', function () {
    $('#pauseButton').css("display", "none");
    $('#runButton').css("display", "block");
    paused = true;
});
var stopAlgorithm = false;
var mycount = 0;
function continueAlgorithm() {
    var countObj = {counter: 0};
    $.ajax({
        type: "POST",
        url: "/continueAlgorithm",
        contentType: "application/json",
        data: JSON.stringify(countObj),
        dataType: 'json',
        cache: false,
        success: function (data) {
            mycount++;
            console.log(mycount);
            for (move in data) {
                console.log(move); // logs state and updates
                // updatePrecinctVisual(move);
            }
            console.log(data);
            updatePrecinctVisual(data);
            var response = JSON.stringify(data, null, 4);
            summaryBox.val(summaryBox.val() + "\n" + response);
            console.log("SUCCESS : ", response);
            if (data.updates.length === 0)
                stopAlgorithm = true;
            if (!stopAlgorithm)
                continueAlgorithm();
            else
                return;
        }
    });
}


// var summaryBox = $('#summaryBox');
// $('#runButton').click(function () {
//     var req;
//     var a = $('#algorithmChoice').val();
//     var m1 = $('#compactnessSlider').val();
//     var m2 = $('#populationSlider').val();
//     var m3 = $('#partisanFairnessSlider').val();
//     var m4 = $('#efficiencyGapSlider').val();
//     console.log(a); // prints rg
//     console.log(m1); // prints value correctly
//     var algObj = {"stateAbbrv": stateSelected, "compactness": m1, "populationEquality": m2, "partisanFairness": m3, "efficencyGap": m4, "algorithm": a};
//     $.ajax({
//         type: "POST",
//         contentType: "application/json",
//         url: "/startAlgorithm",
//         data: JSON.stringify(algObj),
//         dataType: 'json',
//         cache: false,
//         timeout: 600000,
//         success: function (data) {
//             var json = JSON.stringify(data, null, 4);
//             summaryBox.val(summaryBox.val() + "\n" + data["successful"]);
//             console.log("SUCCESS : ", data);
//             delete data["successful"];
//             console.log("after removing successful key ", data);
//             continueAlgorithm();
//         },
//         error: function (e) {
//             var json = "Ajax Response" + e.responseText;
//             summaryBox.val(summaryBox.val() + json);
//             console.log("ERROR : ", e);
//         }
//     });
// });
// var stopAlgorithm = false;
// function continueAlgorithm() {
//     var countObj = {counter: 0};
//     $.ajax({
//         type: "POST",
//         url: "/continueAlgorithm",
//         contentType: "application/json",
//         data: JSON.stringify(countObj),
//         dataType: 'json',
//         cache: false,
//         success: function (data) {
//             var json = JSON.stringify(data, null, 4);
//             summaryBox.val(summaryBox.val() + "\n" + data["successful"]);
//             console.log("SUCCESS : ", data["successful"]);
//             if (data["successful"] === 5)
//                 stopAlgorithm = true;
//             if (!stopAlgorithm)
//                 continueAlgorithm();
//             else
//                 return;
//         }
//     });
// }