function handleBrowseResult(resultData) {
    console.log("handleStarResult: populating star table from resultData");

    let genreTableBodyElement = jQuery("#genre_table_body");

    // Iterate through resultData, no more than 10 entries
    for (let i = 0; i < resultData.length; i++) {

        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + '<a href="index.html?clear=1&page=0&genre_id=' + resultData[i]['genre_id'] + '">' + resultData[i]["genre_name"] + '</a>' + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        genreTableBodyElement.append(rowHTML);
    }
}


/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/browse", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleBrowseResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});