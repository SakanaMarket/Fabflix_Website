/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs two steps:
 *      1. Use jQuery to talk to backend API to get the json data.
 *      2. Populate the data to correct html elements.
 */


/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)");
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

function handleStarResult(resultData) {
    console.log("handleStarResult: populating star table from resultData");

    let moviesTableBodyElement = jQuery("#movies_table_body");
    //let prev_button = document.getElementById("prev");
    let next_button = document.getElementById("next");
    let N_value = getParameterByName("N");
    if (N_value === null || N_value === "")
    {
        N_value = 20;
    }
    console.log(resultData.length);
    console.log(Number(N_value) + 1);
    if (resultData.length === Number(N_value) + 1)
    {
        next_button.style.visibility = "VISIBLE";
    }
    else
    {
        next_button.style.visibility = "HIDDEN";
    }

    let record_len = resultData.length;
    if (record_len > Number(N_value))
    {
        record_len -= 1;
    }
    // Iterate through resultData, no more than 10 entries
    for (let i = 0; i < record_len; i++) {
        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + '<a href="single-movie.html?id=' + resultData[i]['movie_id'] + '">' + resultData[i]["movie_title"] + '</a>' + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";

        let genre_array = resultData[i]["genres"];
        rowHTML += "<th>";
        for( let j = 0; j < genre_array.length; ++j ){
            rowHTML += '<a href="index.html?clear=1&genre_id=' + genre_array[j]["genre_id"] + '">' + genre_array[j]["genre_name"] + '</a>';
        }
        rowHTML += "</th>";

        let cast_array = resultData[i]["stars"];
        rowHTML += "<th>";
        for (let j = 0; j < cast_array.length; ++j ){
            rowHTML += '<a href="single-star.html?id=' + cast_array[j]["star_id"] + '">' + cast_array[j]["star_name"] + '</a>' + " ";
        }
        rowHTML += "</th>";
        rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";

        let movie_id = resultData[i]["movie_id"];

        rowHTML += "<th>" + '<button id=' + movie_id + '>' + "Add to cart" + '</button>' + '</th>';
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        moviesTableBodyElement.append(rowHTML);

        $("#"+String(movie_id)).click( function(){
            jQuery.ajax({
                method: "POST",
                url: "api/shoppingcart?id=" + movie_id + "&quantity=1",
                success: function(){ alert("Success!") },
                error: function(){ alert("Failure!") }
            });
        });
    }
}

function sortTitle()
{
    let url = window.location.href;
    console.log(url);
    let sort_exist = getParameterByName("sort");
    //console.log(url);
    if (sort_exist == null || sort_exist == "")
    {
        if (url.indexOf("?") != -1) // has a ?
        {
            url += "&sort=title";
        }
        else // no ?
        {
            url += "?sort=title";
        }
    }
    else if (sort_exist == "rating")
    {
        url = url.replace("sort=rating", "sort=title")
    }
    //console.log(resetPg(url));
    window.location.replace(resetPg(url));
}

function sortRating()
{
    let url = window.location.href;
    let sort_exist = getParameterByName("sort");
    //console.log(url);
    if (sort_exist == null || sort_exist == "")
    {
        if (url.indexOf("?") != -1) // has a ?
        {
            url += "&sort=rating";
        }
        else // no ?
        {
            url += "?sort=rating";
        }
    }
    else if (sort_exist == "title")
    {
        url = url.replace("sort=title", "sort=rating")
    }
    //console.log(resetPg(url));
    window.location.replace(resetPg(url));
}

function sortASC()
{
    let url = window.location.href;
    let pos = getParameterByName("position");
    if (pos == null || pos == "")
    {
        if (url.indexOf("?") != -1) // has ?
        {
            url += "&position=ASC";
        }
        else
        {
            url += "?position=ASC";
        }
    }
    else if (pos == "DESC")
    {
        url = url.replace("position=DESC", "position=ASC")
    }
    window.location.replace(resetPg(url));
}

function sortDESC()
{
    let url = window.location.href;
    let pos = getParameterByName("position");
    if (pos == null || pos == "")
    {
        if (url.indexOf("?") != -1) // has ?
        {
            url += "&position=DESC";
        }
        else
        {
            url += "?position=DESC";
        }
    }
    else if (pos == "ASC")
    {
        url = url.replace("position=ASC", "position=DESC")
    }
    window.location.replace(resetPg(url));
}

function jumpPage(n)
{
    let url = window.location.href;
    let pg = getParameterByName("page");

    if (pg == null && n >= 1) // first log and home pg and click next
    {
        //console.log("page is home and clicked next");
        if (url.indexOf("?") != -1) // if there are parameters
        {
            url += "&page=1";
        }
        else
        {
            url += "?page=1";
        }
    }
    else if (pg == null && n <= -1)
    {
        //console.log("page is 0 and clicked prev");
        if (url.indexOf("?") != -1) // if there are parameters
        {
            url += "&page=0";
        }
        else
        {
            url += "?page=0";
        }
    }
    else if ((pg > 0 && (n >= 1 || n <= -1)) || (pg == 0 && n >= 1))
    {
        //console.log("page is greater than 0 and clicked next/prev")
        {
            n+=Number(pg);
            url = url.replace(`page=${pg}`, `page=${n}`);
        }
    }
    else if (pg == 0 && n <= -1)
    {
        //console.log("page is 0 and clicked prev");
        {
            n = 0;
            url = url.replace(`page=${pg}`, `page=${n}`);
        }
    }

    window.location.replace(url);
}

function resetPg(url)
{
    let new_url = url.replace(new RegExp(/page=[\d]+/i), "page=0");
    return new_url;
}


let Nform = $("#submitN");

function submitN( formSubmitEvent)
{
    formSubmitEvent.preventDefault();
    let url = window.location.href;
    let N = Nform.serialize();
    console.log("this is N: " + N);
    if (N == null || N == "")
    {
        if (url.indexOf("?") != -1) // has a ?
        {
            url = url.replace(new RegExp(/N=[\d]+/i), "N=20");
            console.log(url);
        }
        else // no ?
        {
            url += "?N=20";
            console.log(url);
        }
    }
    else // if there is already an N or defaulted N = 20
    {
        console.log(N);
        if (url.indexOf("?") != -1) // has a ?
        {
            if (url.indexOf("N=") != -1) // has an N parameter
            {
                url = url.replace(new RegExp(/N=[\d]+/i), N);
            }
            else
            {
                url += "&" + N;
            }
            console.log("in the else 1");
        }
        else // no ?
        {
            url += "?" + N;
            console.log("in the else 2");
        }
    }
    //console.log(resetPg(url));
    window.location.replace(resetPg(url));
}
Nform.submit(submitN);
/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */
let mtitle = getParameterByName("movie_title");
let myear = getParameterByName("movie_year");
let mdir = getParameterByName("movie_director");
let sname = getParameterByName("star_name");
let gid = getParameterByName("genre_id");
let cid = getParameterByName("char_id");
let sort = getParameterByName("sort");
let pos = getParameterByName("position");
let pg = getParameterByName("page");
let N = getParameterByName("N");
let clear = getParameterByName( "clear" );


// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/movies?movie_title=" + mtitle + "&movie_year=" + myear + "&movie_director=" + mdir + "&star_name=" + sname + "&genre_id=" + gid + "&char_id=" + cid +
    "&sort=" + sort + "&position=" + pos + "&page=" + pg + "&N=" + N + "&clear=" + clear, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleStarResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});

////////////////

function handleLookup(query, doneCallback) {
    console.log("autocomplete initiated");
    console.log("sending AJAX request to backend Java Servlet");

    // TODO: if you want to check past query results first, you can do it here

    // sending the HTTP GET request to the Java Servlet endpoint hero-suggestion
    // with the query data
    if (localStorage.getItem(query)==null)
    {
        console.log("Getting results from database...");
        jQuery.ajax({
            "method": "GET",
            // generate the request url from the query.
            // escape the query string to avoid errors caused by special characters
            "url": "api/search?query=" + escape(query),
            "success": function(data) {
                // pass the data, query, and doneCallback function into the success handler
                handleLookupAjaxSuccess(data, query, doneCallback)
            },
            "error": function(errorData) {
                console.log("lookup ajax error")
                console.log(errorData)
            }
        })
    }
    else
    {
        console.log("Getting results from cache...");
        let jsonData = JSON.parse(localStorage.getItem(query));
        console.log(jsonData);
        doneCallback( { suggestions: jsonData } );
    }
}


/*
 * This function is used to handle the ajax success callback function.
 * It is called by our own code upon the success of the AJAX request
 *
 * data is the JSON data string you get from your Java Servlet
 *
 */
function handleLookupAjaxSuccess(data, query, doneCallback) {

    // parse the string into JSON
    let jsonData = JSON.parse(data);
    console.log(jsonData);
    // TODO: if you want to cache the result into a global variable you can do it here
    localStorage.setItem(query, data);

    // call the callback function provided by the autocomplete library
    // add "{suggestions: jsonData}" to satisfy the library response format according to
    //   the "Response Format" section in documentation
    doneCallback( { suggestions: jsonData } );
}


/*
 * This function is the select suggestion handler function.
 * When a suggestion is selected, this function is called by the library.
 *
 * You can redirect to the page you want using the suggestion data.
 */
function handleSelectSuggestion(suggestion) {
    // TODO: jump to the specific result page based on the selected suggestion

    // console.log("you select " + suggestion["value"] + " with ID " + suggestion["data"]["movieId"]);
    window.location.replace("single-movie.html?id="+suggestion["data"]["movieId"]);
}


/*
 * This statement binds the autocomplete library with the input box element and
 *   sets necessary parameters of the library.
 *
 * The library documentation can be find here:
 *   https://github.com/devbridge/jQuery-Autocomplete
 *   https://www.devbridge.com/sourcery/components/jquery-autocomplete/
 *
 */
// $('#autocomplete') is to find element by the ID "autocomplete"
$('#autocomplete').autocomplete({

    // documentation of the lookup function can be found under the "Custom lookup function" section
    lookup: function (query, doneCallback) {
        handleLookup(query, doneCallback)
    },
    onSelect: function(suggestion) {
        handleSelectSuggestion(suggestion)
    },
    lookupLimit: 10,
    minChars: 3,
    // set delay time
    deferRequestBy: 300,
    // there are some other parameters that you might want to use to satisfy all the requirements
    // TODO: add other parameters, such as minimum characters
});


/*
 * do normal full text search if no suggestion is selected
 */
function handleNormalSearch(query) {
    // console.log("doing normal search with query: " + query);
    // TODO: you should do normal search here
}

// bind pressing enter key to a handler function
$('#autocomplete').keypress(function(event) {
    // keyCode 13 is the enter key
    if (event.keyCode == 13) {
        // pass the value of the input box to the handler function
        handleNormalSearch($('#autocomplete').val())
    }
})

// TODO: if you have a "search" button, you may want to bind the onClick event as well of that button

