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
    let cartTableBodyElement = jQuery("#cart_table_body");


    for (let i = 0; i < resultData.length-1; i++)
    {
        let rowHTML = "";
        console.log(resultData[i]);
        let movie_id = resultData[i]["movie_id"];
        let movie_title = resultData[i]["movie_title"];
        let movie_quantity = resultData[i]["movie_quantity"];
        let movie_price = resultData[i]["movie_price"];
        rowHTML += "<tr>";
        rowHTML += "<th>" + movie_title + "</th>";
        rowHTML += "<th>" + "<button id=" + movie_id + "_del" + "> < </button>  " + movie_quantity + "  <button id=" + movie_id + "_add" + "> > </button>" + "</th>";
        rowHTML += "<th>$" + movie_price + "</th>";
        rowHTML += "<th><button id=" + movie_id + "_delall" + ">Delete</button></th>";
        rowHTML += "<tr>";

        cartTableBodyElement.append(rowHTML);

        $("#"+String(movie_id)+"_add").click( function(){
            console.log("Increasing");
            jQuery.ajax({
                method: "POST",
                url: "api/shoppingcart?id=" + movie_id + "&quantity=1",
                success: function(){window.location.reload()},
                error: function(){ alert("Failure!") }
            });
        });

        $("#"+String(movie_id)+"_del").click( function(){
            console.log("Decreasing");
            jQuery.ajax({
                method: "POST",
                url: "api/shoppingcart?id=" + movie_id + "&quantity=-1",
                success: function(){window.location.reload()},
                error: function(){ alert("Failure!") }
            });
        });

        $("#"+String(movie_id)+"_delall").click( function(){
            console.log("Deleting Item");
            jQuery.ajax({
                method: "POST",
                url: "api/shoppingcart?id=" + movie_id + "&quantity=" + -(Math.abs(movie_quantity)),
                success: function(){window.location.reload()},
                error: function(){ alert("Failure!") }
            });
        });
    }

    let total = document.getElementById("total");
    let total_text = resultData[resultData.length-1]["total_price"];
    total.append(total_text);

}

function changeQuantity(button_id){

}

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/shoppingcart", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});