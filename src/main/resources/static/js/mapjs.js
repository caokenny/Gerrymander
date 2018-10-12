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

//L.geoJSON(kansasgeo, {
//    style: function (feature) {
//        if (feature.properties.KS_GEO_ID === 1) {
//            return {fillColor: "red"};
//        }
//        if (feature.properties.KS_GEO_ID === 2) {
//            return {fillColor: "black"};
//        }
//    }
//});


var initialStyle = {color: "black", opacity: 1, fillColor: "orange", fillOpacity: 1};


var coloradoEvent = L.geoJSON(colorado, {
    style: function() {
        return {color: "black", opacity: 1, fillColor: "orange", fillOpacity: 1}
    }
}).addTo(mymap);

coloradoEvent.on('click', function(event) {
    mymap.fitBounds(event.layer.getBounds())
});

coloradoEvent.on('mouseover', function () {
    this.setStyle({
        color: 'white'
    });
});

coloradoEvent.on('mouseout', function () {
    this.setStyle(initialStyle);
});

var kansasEvent = L.geoJSON(kansas, {
    style: function() {
        return {color: "black", opacity: 1, fillColor: "orange", fillOpacity: 1}
    }
}).addTo(mymap);

kansasEvent.on('click', function(event) {
    mymap.fitBounds(event.layer.getBounds())
});

kansasEvent.on('mouseover', function () {
    this.setStyle({
        color: 'white'
    });
});

kansasEvent.on('mouseout', function () {
    this.setStyle(initialStyle);
});


var missouriEvent = L.geoJSON(missouri, {
    style: function() {
        return {color: "black", opacity: 1, fillColor: "orange", fillOpacity: 1}
    }
}).addTo(mymap);

missouriEvent.on('click', function(event) {
    mymap.fitBounds(event.layer.getBounds())
});

missouriEvent.on('mouseover', function () {
    this.setStyle({
        color: 'white'
    });
});

missouriEvent.on('mouseout', function () {
    this.setStyle(initialStyle);
});

function goHome() {
    mymap.setView([37.0902, -95.7129], 4);
}