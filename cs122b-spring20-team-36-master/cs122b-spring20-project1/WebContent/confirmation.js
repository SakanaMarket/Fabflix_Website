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


function handleResult(resultData)
{
    let confirmTableBodyElement = jQuery("#confirm_table_body");


    for (let i = 0; i < resultData.length-1; i++)
    {
        let rowHTML = "";
        console.log(resultData[i]);
        let movie_title = resultData[i]["title"];
        let movie_quantity = resultData[i]["quantity"];
        let sale_id = resultData[i]["sale_id"]
        rowHTML += "<tr>";
        rowHTML += "<th>" + movie_title + "</th>";
        rowHTML += "<th>" + movie_quantity + "</th>";
        rowHTML += "<th>" + sale_id + "</th>";
        rowHTML += "<tr>";

        confirmTableBodyElement.append(rowHTML);
    }

    let total = document.getElementById("total");
    let total_text = resultData[resultData.length-1]["total_price"];
    total.append(total_text);

}

function test(resultData)
{
    console.log(resultData);
}

jQuery.ajax({
    dataType: "json", // Setting return data type
    data: {trans_id: getParameterByName("transaction_ids"), cart_total: getParameterByName("total_price")},
    method: "GET", // Setting request method
    url: "api/confirmation", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});



