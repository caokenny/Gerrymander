<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html lang="en">
<head>
    <%--<meta charset="UTF-8">--%>
    <title>Team Falcon</title>
    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.3.4/dist/leaflet.css"
          integrity="sha512-puBpdR0798OZvTTbP4A8Ix/l+A4dHDD0DGqYW6RQ+9jxkRFclaxxQb/SJAWZfWAkuyeQUytO7+7N4QKrDh+drA=="
          crossorigin=""/>
    <link rel="stylesheet" type="text/css" href="/css/indexCSS.css"/>


</head>
<body>
<div id="wrapper">

    <div id="loginDiv">
        <form id="logincontent" method="post">
            <span class="closeLoginBox" onclick="closeLogin()">&times;</span>
            <input type="text" name="username" id="loginUsername" placeholder="Enter Username" required>
            <input type="password" name="password" id="loginPassword" placeholder="Enter Password" required>
            <button class="loginSubmit" type="submit">Login</button>
        </form>
    </div>

    <div id="registerDiv">
        <form id="registercontent">
            <span class="closeLoginBox" onclick="closeRegister()">&times;</span>
            <input type="text" id="registerUsername" placeholder="Enter Username" required>
            <input type="password" id="registerPassword" placeholder="Enter Password" required>
            <input type="password" placeholder="Verify Password" required>
            <input type="email" id="registerEmail" placeholder="Enter Email Address" required>
            <button class="registerSubmit" type="submit">Register</button>
        </form>
    </div>

    <div id="mapheader">
        <ul id="nav">
            <c:choose>
                <c:when test="${empty loggedInUser}">
                    <li><a href="#" onclick="goHome()">Home</a></li>
                    <li><a href="#" onclick="popupLogin()">Log In</a></li>
                    <li><a href="#" onclick="popupRegister()">Register</a></li>
                </c:when>
                <c:otherwise>
                    <li><a href="#" onclick="goHome()">Home</a></li>
                    <li>Welcome ${username}</li>
                </c:otherwise>
            </c:choose>
        </ul>
    </div>

    <div id="mapid"></div>


    <div id="usercontrol">
        <div id="welcomeDiv">
            <h1 id="welcomeMessage">Welcome <br/> to <br/> Team Falcon's <br/> Project. <br/> Click on a state to continue.</h1>
            <button class="colorChange" value="red">Red</button>
            <button class="colorChange" value="blue">Blue</button>
            <button class="colorChange" value="white">White</button>
        </div>
        <div id="buttons">
            <button class="algoButtons" type="button" style="margin-right: 20px;">Update</button>
            <button class="algoButtons" type="button">Run</button>
        </div>
        <div id="algorithmChoiceDiv">
            <select id="algorithmChoice">
                <option value="sa">Simulated Annealing</option>
                <option value="rg">Region Growing</option>
            </select>
        </div>
        <div id="measuresContainer">
            <h2 class="measureSliderTitle">Compactness</h2>
            <input type="range" min="1" max="100" value="50" class="measureSlider" id="compactnessSlider">
            <h2 class="measureSliderTitle">Population</h2>
            <input type="range" min="1" max="100" value="50" class="measureSlider" id="populationSlider">
            <h2 class="measureSliderTitle">Partisan Fairness</h2>
            <input type="range" min="1" max="100" value="50" class="measureSlider" id="partisanFairnessSlider">
            <h2 class="measureSliderTitle">Efficiency Gap</h2>
            <input type="range" min="1" max="100" value="50" class="measureSlider" id="efficiencyGapSlider">
        </div>
        <textarea id="summaryBox" readonly>Summary</textarea>



    </div>
</div>

    <script src="https://unpkg.com/leaflet@1.3.4/dist/leaflet.js"
            integrity="sha512-nMMmRyTVoLYqjP9hrbed9S+FzjZHW5gY1TWCHA5ckwXZBadntCNs8kEqAWdrb9O7rxbCaA4lKTIWjDXZxflOcA=="
            crossorigin=""></script>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>
    <script src="/js/us-states.js"></script>
    <script src="/js/continents.js"></script>
    <script src="/js/KS_final.js"></script>
    <script src="/js/Colorado.js"></script>
    <script src="/js/Kansas.js"></script>
    <script src="/js/Missouri.js"></script>
    <script src="/js/indexjs.js"></script>
    <script src="/js/mapjs.js"></script>

</body>
</html>