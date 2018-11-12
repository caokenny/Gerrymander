var mymap = L.map('mapid', {zoomControl: false}).setView([37.0902, -95.7129], 4);
mymap.dragging.disable();
mymap.doubleClickZoom.disable();
mymap.scrollWheelZoom.disable();

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

var onColorado = false;
var onKansas = false;
var onMissouri = false;


var coloradoEvent = L.geoJSON(colorado, {
    style: function() {
        return {color: "black", opacity: 1, fillColor: "orange", fillOpacity: 1}
    }
}).addTo(mymap);

var kansasEvent = L.geoJSON(kansas, {
    style: function() {
        return {color: "black", opacity: 1, fillColor: "orange", fillOpacity: 1}
    }
}).addTo(mymap);

var missouriEvent = L.geoJSON(missouri, {
    style: function() {
        return {color: "black", opacity: 1, fillColor: "orange", fillOpacity: 1}
    }
}).addTo(mymap);

coloradoEvent.on('click', function(event) {
    if (onKansas === false && onMissouri === false) {
        onColorado = true;
        document.getElementById("welcomeDiv").style.display = "none";
        document.getElementById("buttons").style.display = "flex";
        document.getElementById("measuresContainer").style.display = "flex";
        document.getElementById("summaryBox").style.display = "block";
        document.getElementById("usercontrol").style.backgroundColor = "black";
        document.getElementById("algorithmChoiceDiv").style.display = "flex";
        kansasEvent.setStyle({fillColor: "blue", fillOpacity: 1});
        missouriEvent.setStyle({fillColor: "blue", fillOpacity: 1});
        mymap.fitBounds(event.layer.getBounds());
    }
});

coloradoEvent.on('mouseover', function () {
    if (onColorado === false && onKansas === false && onMissouri === false) {
        this.setStyle({
            color: 'white'
        });
    } else if (onColorado === true) {
        this.setStyle(initialStyle);
    } else {
        this.setStyle({fillColor: "blue", fillOpacity: 1});
    }
});

coloradoEvent.on('mouseout', function () {
    if (onKansas === true || onMissouri === true) {
        this.setStyle({fillColor: "blue", fillOpacity: 1});
    } else {
        this.setStyle(initialStyle);
    }
});

kansasEvent.on('click', function(event) {
    if (onColorado === false && onMissouri === false) {
        onKansas = true;
        document.getElementById("welcomeDiv").style.display = "none";
        document.getElementById("buttons").style.display = "flex";
        document.getElementById("measuresContainer").style.display = "flex";
        document.getElementById("summaryBox").style.display = "block";
        document.getElementById("usercontrol").style.backgroundColor = "black";
        document.getElementById("algorithmChoiceDiv").style.display = "block";
        coloradoEvent.setStyle({fillColor: "blue", fillOpacity: 1});
        missouriEvent.setStyle({fillColor: "blue", fillOpacity: 1});
        mymap.fitBounds(event.layer.getBounds());
        mymap.dragging.enable();
        mymap.doubleClickZoom.enable();
        mymap.scrollWheelZoom.enable();
        zoomKansas();
    }
});

kansasEvent.on('mouseover', function () {
    if (onColorado === false && onKansas === false && onMissouri === false) {
        this.setStyle({
            color: 'white'
        });
    } else if (onKansas === true) {
        this.setStyle(initialStyle);
    } else {
        this.setStyle({fillColor: "blue", fillOpacity: 1});
    }
});

kansasEvent.on('mouseout', function () {
    if (onColorado === true || onMissouri === true) {
        this.setStyle({fillColor: "blue", fillOpacity: 1});
    } else {
        this.setStyle(initialStyle);
    }
});

missouriEvent.on('click', function(event) {
    if (onColorado === false && onKansas === false) {
        onMissouri = true;
        document.getElementById("welcomeDiv").style.display = "none";
        document.getElementById("buttons").style.display = "flex";
        document.getElementById("measuresContainer").style.display = "flex";
        document.getElementById("summaryBox").style.display = "block";
        document.getElementById("usercontrol").style.backgroundColor = "black";
        document.getElementById("algorithmChoiceDiv").style.display = "block";
        kansasEvent.setStyle({fillColor: "blue", fillOpacity: 1});
        coloradoEvent.setStyle({fillColor: "blue", fillOpacity: 1});
        mymap.fitBounds(event.layer.getBounds());
    }
});

missouriEvent.on('mouseover', function () {
    if (onColorado === false && onKansas === false && onMissouri === false) {
        this.setStyle({
            color: 'white'
        });
    } else if (onMissouri === true) {
        this.setStyle(initialStyle);
    } else {
        this.setStyle({fillColor: "blue", fillOpacity: 1});
    }
});

missouriEvent.on('mouseout', function () {
    if (onColorado === true || onKansas === true) {
        this.setStyle({fillColor: "blue", fillOpacity: 1});
    } else {
        this.setStyle(initialStyle);
    }
});


function goHome() {
    onColorado = false;
    onKansas = false;
    onMissouri = false;
    coloradoEvent.setStyle(initialStyle);
    kansasEvent.setStyle(initialStyle);
    missouriEvent.setStyle(initialStyle);
    mymap.setView([37.0902, -95.7129], 4);
    document.getElementById("buttons").style.display = "none";
    document.getElementById("measuresContainer").style.display = "none";
    document.getElementById("summaryBox").style.display = "none";
    document.getElementById("usercontrol").style.backgroundColor = "orange";
    document.getElementById("welcomeDiv").style.display = "block";
    document.getElementById("algorithmChoiceDiv").style.display = "none";
}

// function zoomKansas() {
//     L.geoJSON(kansasgeo, {
//         style: function (feature) {
//             if (feature.properties.KS_GEO_ID === 7) {
//                 return {fillColor: "red", fillOpacity: 1, color: "black"};
//             }
//             else if (feature.properties.KS_GEO_ID === 8) {
//                 return {fillColor: "yellow", fillOpacity: 1, color: "black"};
//             }
//             else if (feature.properties.KS_GEO_ID === 11) {
//                 return {fillColor: "green", fillOpacity: 1, color: "black"};
//             }
//             else if (feature.properties.KS_GEO_ID === 10) {
//                 return {fillColor: "purple", fillOpacity: 1, color: "black"};
//             } else {
//                 return {color: "black"}
//             }
//         }
//     }).addTo(mymap);
// }


// L.geoJSON(kansasgeo, {
//    style: function (feature) {
//        if (feature.properties.KS_GEO_ID === 6) {
//            return {fillColor: "red", fillOpacity: 1};
//        }
//        if (feature.properties.KS_GEO_ID === 7) {
//            return {fillColor: "black", fillOpacity: 1};
//        }
//        if (feature.properties.KS_GEO_ID === 10) {
//            return {fillColor: "green", fillOpacity: 1};
//        }
//        if (feature.properties.KS_GEO_ID === 9) {
//            return {fillColor: "purple", fillOpacity: 1};
//        }
//    }
// }).addTo(mymap);