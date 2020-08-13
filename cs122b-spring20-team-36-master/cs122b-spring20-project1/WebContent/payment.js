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


function handlePaymentResult( resultData ){
    console.log( "handle payment result" );
    if( resultData[0]["status"] == "success" ){
        console.log(resultData);
        let input = document.getElementById("hidden_form");
        let inside_input = document.getElementById("result");

        inside_input.setAttribute("value", JSON.stringify(resultData[1]));
        console.log("look under");
        console.log(resultData);

        input.submit();
        console.log("set hidden attr");
    }
    else if (resultData[0]["status"] == "failure")
    {
        let confirm_label = document.getElementById("confirm");
        confirm_label.innerHTML = resultData[0]["message"];
    }

}

let paymentForm = $("#payment_form");

function submitPaymentForm( formSubmitEvent ) {
    console.log("submit payment form");
    formSubmitEvent.preventDefault();
    jQuery.ajax({
        dataType: "json",
        data: paymentForm.serialize(),
        method: "POST", // Setting request method
        url: "api/payment",
        success: resultData => handlePaymentResult( resultData )
    });
}

paymentForm.submit( submitPaymentForm );

function handleShoppingResult(resultData) {
    let label = document.getElementById("total_price");
    let total_price = resultData[resultData.length - 1]["total_price"];
    if (total_price != null) {
        label.textContent = "Total Price: $" + resultData[resultData.length - 1]["total_price"];
        let inside_total = document.getElementById("total");
        inside_total.setAttribute("value", resultData[resultData.length - 1]["total_price"]);
    } else {
        label.textContent = "Total Price: $0";
    }
}

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/shoppingcart", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleShoppingResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});