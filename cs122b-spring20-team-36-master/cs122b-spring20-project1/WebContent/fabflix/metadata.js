
function handleTableResult( resultJsonData ){
    console.log( resultJsonData );
    console.log( "Handling table result data" );
    for( let i = 0; i < resultJsonData.length; ++i ){
        let title = document.createElement("B");
        let table = document.createElement( "table" );
        table.className = "table table-striped";
        title.innerHTML = resultJsonData[i]["tableName"];
        for( let j = 0; j < resultJsonData[i]["attributes"].length; ++j ) {
            let row = document.createElement( "tr" );
            let field = document.createElement("td");
            let type = document.createElement("td");
            field.innerHTML = resultJsonData[i]["attributes"][j]["field"];
            type.innerHTML = resultJsonData[i]["attributes"][j]["type"];
            row.appendChild( field );
            row.appendChild( type );
            table.appendChild( row );
        }
        document.getElementById("body").appendChild( title );
        document.getElementById("body").appendChild( table );
    }
}

jQuery.ajax({
    dataType: "json",
    url: "../api/metadata",
    method: "GET",
    success: resultData => handleTableResult( resultData )
});