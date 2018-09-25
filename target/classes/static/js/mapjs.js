var mymap = L.map('mapid', {zoomControl: false}).setView([37.0902, -95.7129], 4);

var mapboxtile = L.tileLayer('https://api.mapbox.com/styles/v1/mapbox/emerald-v8/tiles/{z}/{x}/{y}?access_token={accessToken}', {
    attribution: 'Map data &copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors, <a href="https://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="https://www.mapbox.com/">Mapbox</a>',
    maxZoom: 18,
    id: 'mapbox.streets',
    accessToken: 'pk.eyJ1IjoiY2Fva2VubnkiLCJhIjoiY2ptZHhzcmJoMHVlYjNwbW90cm1kZW11bSJ9.C6aOC-2bLmc9SIXXjI0tyQ'
});

mapboxtile.addTo(mymap);

// mymap.dragging.disable();
// mymap.touchZoom.disable();
// mymap.doubleClickZoom.disable();
// mymap.scrollWheelZoom.disable();

//Add geoJSON

L.geoJSON(usageo, {
    style: function () {
        return {color: "blue"};
    }
}).addTo(mymap);