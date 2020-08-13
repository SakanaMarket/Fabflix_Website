
let arr = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ*";
function handleCharacterResult( charArray ) {
    console.log("handleStarResult: populating star table from resultData");

    let titleTableBodyElement = jQuery("#title_table_body");

    // Iterate through resultData, no more than 10 entries
    for (let i = 0; i < charArray.length; i++) {

        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + '<a href="index.html?clear=1&page=0&char_id=' + charArray[i] + '">' + charArray[i] + '</a>' + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        titleTableBodyElement.append(rowHTML);
    }
}

handleCharacterResult( arr );