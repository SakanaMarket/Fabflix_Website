let star_form = $("#star_form");

function handleStarResult( resultDataJson ){
    alert(resultDataJson["message"]);
    star_form[0].reset();
}

function submitStarForm( formSubmitEvent ){
    console.log("submit star form");
    formSubmitEvent.preventDefault();
    jQuery.ajax({
        dataType: "json",
        data: star_form.serialize(),
        method: "POST", // Setting request method
        url: "../api/newstar",
        success: (resultData) => handleStarResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
    });
}

star_form.submit( submitStarForm );