let login_form = $("#login_form");

function handleLoginResult( resultDataJson ){
    console.log("handle login response");
    console.log(resultDataJson);
    console.log(resultDataJson["status"]);

    if( resultDataJson["status"] === "success" ) {
        window.location.replace("index.html");
    } else {
        console.log("show error message");
        console.log(resultDataJson["message"]);
        grecaptcha.reset();
        $("#login_info").text( resultDataJson["message"] );
    }
}


function submitLoginForm( formSubmitEvent ){
    console.log("submit login form");
    formSubmitEvent.preventDefault();
    jQuery.ajax({
        dataType: "json",
        data: login_form.serialize(),
        method: "POST", // Setting request method
        url: "api/login",
        success: (resultData) => handleLoginResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
    });
}


login_form.submit( submitLoginForm );