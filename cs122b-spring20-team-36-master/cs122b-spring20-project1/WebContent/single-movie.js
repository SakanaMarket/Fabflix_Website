/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs three steps:
 *      1. Get parameter from request URL so it know which id to look for
 *      2. Use jQuery to talk to backend API to get the json data.
 *      3. Populate the data to correct html elements.
 */


/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {

    // populate the star info h3
    // find the empty h3 body by id "star_info"
    let movieInfoElement = jQuery("#movie_info");

    //append two html <p> created to the h3 body, which will refresh the page
    movieInfoElement.append("<p>Movie Name: " + resultData["movie_title"] + "</p>" +
        "<p>Release Year: " + resultData["movie_year"] + "</p>" +
        "<p>Director: " + resultData["movie_director"] + "</p>" );

    if( resultData["movie_rating"] == null ){
        movieInfoElement.append( "<p>Rating: N/A</p>" );
    } else {
        movieInfoElement.append( "<p>Rating: " + resultData["movie_rating"] + "</p>" );
    }

    let g_array = resultData["genres"];
    let genreHTML = ""
    for( let i = 0; i < g_array.length; ++i ){
        genreHTML += '<a href="index.html?genre_id=' + g_array[i]["genre_id"] + '">' + g_array[i]["genre_name"] + " " + '<a/>';
    }

    movieInfoElement.append( genreHTML );

    let movie_id = getParameterByName('id');
    console.log(movie_id);
    let add =  "<br><br><p>" + '<button id=' + movie_id + '>' + "Add to cart" + '</button>' + '</p>';

    movieInfoElement.append(add);
    $("#"+String(movie_id)).click( function(){
        jQuery.ajax({
            method: "POST",
            url: "api/shoppingcart?id=" + movie_id + "&quantity=1",
            success: function(){ alert("Success!") },
            error: function(){ alert("Failure!") }
        });
    });

    console.log("handleResult: populating movie table from resultData~~~~");

    // Populate the star table
    // Find the empty table body by id "movie_table_body"
     let starTableBodyElement = jQuery("#star_table_body");
    // Concatenate the html tags with resultData jsonObject to create table rows
    let rowHTML = "";

    let star_array = resultData["stars"];

    for ( let i = 0; i < star_array.length; ++i ) {
        rowHTML += "<tr>";
        rowHTML += "<th>";
        rowHTML += '<a href="single-star.html?id=' + star_array[i]["star_id"] + '">' + star_array[i]["star_name"] + " " + '</a>';
        rowHTML += "</th>";
        rowHTML += "</tr>";
    }

    // Append the row created to the table body, which will refresh the page
    starTableBodyElement.append(rowHTML);
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let movieId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-movie?id=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});