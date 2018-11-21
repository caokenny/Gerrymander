var mymap = L.map('mapid', {zoomControl: false}).setView([37.0902, -95.7129], 4);
// mymap.dragging.disable();
mymap.doubleClickZoom.disable();
// mymap.scrollWheelZoom.disable();

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

var continents = L.geoJSON(continentsgeo, {
    style: function (feature) {
        if (feature.properties.CONTINENT !== "North America") {
            return {color: "black", opacity: 1, fillColor: "black", fillOpacity: 1};
        } else {
            return {color: "black"};
        }
    }
}).addTo(mymap);


var initialStyle = {color: "black", opacity: 1, fillColor: "orange", fillOpacity: 1};

var statesArr = [
    colorado,
    kansas,
    missouri
];

var stateNamesArr = [
    "colorado",
    "kansas",
    "missouri"
];

var geoJSONEvents = [];

var i;
for (i = 0; i < statesArr.length; i++) {
    geoJSONEvents[i] = {stateName : stateNamesArr[i], geo : L.geoJSON(statesArr[i], {
            style: function () {
                return {color: "black", opacity: 1, fillColor: "orange", fillOpacity: 1}
            }
        }).addTo(mymap)};
}

var onStateAlready = false;

for (i = 0; i < geoJSONEvents.length; i++) {
    geoJSONEvents[i].geo.on('click', function (event) {
        console.log(event);
        zoomState(event, this);
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

// $('#selectStateSubmit').click(function (event) {
//     var selectMenu = document.getElementById("stateSelectMenu");
//     var selected = selectMenu.options[selectMenu.selectedIndex].value;
//     var i;
//     for (i = 0; i < geoJSONEvents.length; i++) {
//         if (geoJSONEvents[i].stateName === selected) {
//             $(geoJSONEvents[i].geo).trigger("click");
//         }
//     }
// });

function zoomState(event, geoObj) {
    if (!onStateAlready) {
        onStateAlready = true;
        document.getElementById("welcomeDiv").style.display = "none";
        document.getElementById("buttons").style.display = "flex";
        document.getElementById("measuresContainer").style.display = "flex";
        document.getElementById("summaryBox").style.display = "block";
        document.getElementById("usercontrol").style.backgroundColor = "black";
        document.getElementById("algorithmChoiceDiv").style.display = "flex";
        var j;
        for (j = 0; j < geoJSONEvents.length; j++) {
            geoJSONEvents[j].geo.setStyle({fillColor: "blue", fillOpacity: 1});
        }
        geoObj.setStyle(initialStyle);
        L.geoJSON(kansasgeo, {
            style: function (feature) {
                if (feature.properties.KS_GEO_ID === 10) {
                    return {fillColor: "red", fillOpacity: 1};
                }
                if (feature.properties.KS_GEO_ID === 7) {
                    return {fillColor: "black", fillOpacity: 1};
                }
                if (feature.properties.KS_GEO_ID === 11) {
                    return {fillColor: "#FFFF00", fillOpacity: 1};
                }
                // if (feature.properties.KS_GEO_ID === 9) {
                //     return {fillColor: "purple", fillOpacity: 1};
                // }
                // if (feature.properties.KS_GEO_ID === 16) {
                //     return {fillColor: "yellow", fillOpacity: 1};
                // }
                // if (feature.properties.KS_GEO_ID === 17) {
                //     return {fillColor: "white", fillOpacity: 1};
                // }
            }
        }).addTo(mymap);
        mymap.fitBounds(event.layer.getBounds());
    }
}



function goHome() {
    onStateAlready = false;
    var j;
    for (j = 0; j < geoJSONEvents.length; j++) {
        geoJSONEvents[j].geo.setStyle(initialStyle);
    }
    mymap.setView([37.0902, -95.7129], 4);
    document.getElementById("buttons").style.display = "none";
    document.getElementById("measuresContainer").style.display = "none";
    document.getElementById("summaryBox").style.display = "none";
    document.getElementById("usercontrol").style.backgroundColor = "orange";
    document.getElementById("welcomeDiv").style.display = "block";
    document.getElementById("algorithmChoiceDiv").style.display = "none";
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