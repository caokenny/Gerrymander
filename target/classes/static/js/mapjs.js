var mymap = L.map('mapid', {zoomControl: false}).setView([37.0902, -95.7129], 5);
mymap.doubleClickZoom.disable();

var mapboxtile = L.tileLayer('https://api.mapbox.com/styles/v1/mapbox/emerald-v8/tiles/{z}/{x}/{y}?access_token={accessToken}', {
    attribution: 'Map data &copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors, <a href="https://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="https://www.mapbox.com/">Mapbox</a>',
    minZoom: 4,
    id: 'mapbox.streets',
    accessToken: 'pk.eyJ1IjoiY2Fva2VubnkiLCJhIjoiY2ptZHhzcmJoMHVlYjNwbW90cm1kZW11bSJ9.C6aOC-2bLmc9SIXXjI0tyQ'
}).addTo(mymap);

var colors = {"01" : "red", "02" : "green", "03" : "purple", "04" : "#489ec9", "05" : "orange", "06" : "pink", "07" : "gray", "08" : "brown"};

//Add geoJSON

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
var oldBounds;
var oldGeoObj;
var oldStateName;
var originalPrecinctLayer;
function zoomState(bounds, geoObj, stateName) {
    oldBounds = bounds;
    oldGeoObj = geoObj;
    oldStateName = stateName;
    stateSelected = geoObj.options.abbr;
    if (!onStateAlready) {
        onStateAlready = true;
        // $('#mySidenav').css("width", "400px");
        $('#stateDropdown').css("display", "none");
        $('#otherSideNavLinks').css("display", "none");
        $('.userLog').css("display", "none");
        $('#adminSettings').css("display", "none");
        $('#usercontrol').css("display", "flex");
        $('#openSideNav').trigger('click');
        // $('.sidenav a').css("font-size", "20px");
        // $('.sidenav div').css("font-size", "20px");
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
        mymap.flyTo(bounds.getCenter(), zoomLevel);
        // mymap.fitBounds(bounds);
        $.getJSON("/js/json/" + stateName + "_final.json", function (data) {
            precinctLayer = L.geoJSON(data, {
                style: function (feature) {
                    return {color: "#DCDCDC", opacity: 0.5, fillColor: colors[feature.properties.DISTRICT], fillOpacity: 1};
                },
                onEachFeature: function(feature, layer) {
                    layer.on('mouseover', function () {
                        $('#infoPopup').css("display", "block");
                        $('#precinctname').text(feature.properties.NAME10);
                        $('#caucasian').text("Caucasian: " + Math.floor(feature.properties.CAUCASIAN * feature.properties.POP100));
                        $('#africanAmerican').text("African American: " + Math.floor(feature.properties.AFRICAN_AMERICAN * feature.properties.POP100));
                        $('#americanIndian').text("American Indian: " + Math.floor(feature.properties.AMERICAN_INDIAN * feature.properties.POP100));
                        $('#asian').text("Asian: " + Math.floor(feature.properties.ASIAN * feature.properties.POP100));
                        $('#twoOrMoreRaces').text("Two or More Races: " + Math.floor(feature.properties.TWO_OR_MORE_RACES * feature.properties.POP100));
                        $('#hispanic').text("Hispanic: " + Math.floor(feature.properties.HISPANIC * feature.properties.POP100));
                        $('#populationInfo').text("Total Population: " + feature.properties.POP100);
                        $('#presidentialD').text("Democrat: " + parseInt(feature.properties.PRES_D_08));
                        $('#presidentialR').text("Republican: " + parseInt(feature.properties.PRES_R_08));
                    });
                    layer.on('mouseout', function () {
                        $('#infoPopup').css("display", "none");
                    });
                },
                name: stateName,
                abbr: geoObj.abbr
            });
            originalPrecinctLayer = L.geoJSON(data, {
                style: function (feature) {
                    return {color: "#DCDCDC", opacity: 0.5, fillColor: colors[feature.properties.DISTRICT], fillOpacity: 1};
                },
                onEachFeature: function(feature, layer) {
                    layer.on('mouseover', function () {
                        $('#infoPopup').css("display", "block");
                        $('#precinctname').text(feature.properties.NAME10);
                        $('#caucasian').text("Caucasian: " + Math.floor(feature.properties.CAUCASIAN * feature.properties.POP100));
                        $('#africanAmerican').text("African American: " + Math.floor(feature.properties.AFRICAN_AMERICAN * feature.properties.POP100));
                        $('#americanIndian').text("American Indian: " + Math.floor(feature.properties.AMERICAN_INDIAN * feature.properties.POP100));
                        $('#asian').text("Asian: " + Math.floor(feature.properties.ASIAN * feature.properties.POP100));
                        $('#twoOrMoreRaces').text("Two or More Races: " + Math.floor(feature.properties.TWO_OR_MORE_RACES * feature.properties.POP100));
                        $('#hispanic').text("Hispanic: " + Math.floor(feature.properties.HISPANIC * feature.properties.POP100));
                        $('#populationInfo').text("Total Population: " + feature.properties.POP100);
                        $('#presidentialD').text("Democrat: " + parseInt(feature.properties.PRES_D_08));
                        $('#presidentialR').text("Republican: " + parseInt(feature.properties.PRES_R_08));
                    });
                    layer.on('mouseout', function () {
                        $('#infoPopup').css("display", "none");
                    });
                },
                name: stateName,
                abbr: geoObj.abbr
            });
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
    mymap.flyTo([37.0902, -95.7129], 5);
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
    if ($('#algorithmChoice').val() === "-1") {
        alert("Please select an algorithm");
    } else {
        $('#runButton').prop("disabled", false);
        $.ajax({
            url: "/rg/pickrgseed",
            async: true,
            // dataType: "json",
            type: "POST",
            // contentType: "application/json",
            data: {"stateName": stateSelected, "seedNum": 5},
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
    }
});

$('#algorithmChoice').change(function () {
    var algorithm = $('#algorithmChoice').val();
    if (algorithm === "rg") {
        $('#runButton').prop("disabled", true);
        $('#updateButton').prop("disabled", false);
    } else {
        $('#updateButton').prop("disabled", true);
    }
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
                } else {
                    $('#stopButton').css("display", "none");
                    $('#pauseButton').css("display", "none");
                    $('#updateButton').css("display", "none");
                    $('#resetButton').css("display", "block");
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
    if (!paused) {
        $('#runButton').trigger('click');
    }
}


$('#pauseButton').on('click', function () {
    $('#pauseButton').css("display", "none");
    $('#runButton').css("display", "block");
    paused = true;
});
var stopAlgorithm = false;
$('#stopButton').on('click', function () {
    $('#pauseButton').css("display", "none");
    $('#stopButton').css("display", "none");
    $('#runButton').css("display", "none");
    $('#resetButton').css("display", "block");
    $('#updateButton').css("display", "none");
    paused = true;
});

$('#resetButton').on('click', function () {
    var j;
    for (j = 0; j < stateEvents.length; j++) {
        stateEvents[j].setStyle(initialStyle);
    }
    mymap.removeLayer(precinctLayer);
    mymap.removeLayer(districtLayer);
    onStateAlready = false;
    precinctLayer = null;
    districtLayer = null;

    $('#resetButton').css("display", "none");
    $('#runButton').css("display", "block");
    $('#updateButton').css("display", "block");
    zoomState(oldBounds, oldGeoObj, oldStateName);
    paused = false;
});
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

$(document).ready(function(){
    $ ("#searchForm"). hide ();
    $("#searchButton").click(function(){
        $("#searchForm").show();
    });
});

function handleSearch() {
    var input = document.getElementById("search").value;
    input = input.toLowerCase();
    var found = false;
    for (var i = 0; i < stateEvents.length; i++) {
        if (stateEvents[i].options.name === input) {
            found = true;
            stateEvents[i].fire('click');
            console.log('fired');
            break;
        }
    }
    if (found === false) {
        for (var j = 0; j < allStates.length; j++) {
            if (allStates[j].name === input) {
                mymap.flyTo(allStates[j].coordinates, 7);
                break;
            }
        }
    }
}

$('#toggledBtn').on('click', function () {
    if (mymap.hasLayer(precinctLayer)) {
        console.log("has precinctLayer");
        mymap.removeLayer(precinctLayer);
        mymap.addLayer(originalPrecinctLayer);
    } else {
        console.log("HELLO");
        mymap.removeLayer(originalPrecinctLayer);
        mymap.addLayer(precinctLayer);
    }
});
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