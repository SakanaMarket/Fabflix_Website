let movie_form = $("#movie_form");

function handleMovieResult( resultDataJson ){
    alert(resultDataJson["message"]);
    movie_form[0].reset();
}

function submitMovieForm( formSubmitEvent ){
    console.log("submit movie form");
    formSubmitEvent.preventDefault();
    jQuery.ajax({
        dataType: "json",
        data: movie_form.serialize(),
        method: "POST", // Setting request method
        url: "../api/newmovie",
        success: (resultData) => handleMovieResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
    });
}

movie_form.submit( submitMovieForm );