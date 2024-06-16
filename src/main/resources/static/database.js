$(document).ready( function () {
    var table = $('#FileTable').DataTable({
        "sAjaxSource": "/previousupload",
        "sAjaxDataProp": "",
        "order": [[ 0, "asc" ]],
        "aoColumns": [
            { "mData": "Taskid"},
            { "mData": "file_name" },
            { "mData": "clamav_results" },
            { "mData": "static_results" },
            { "mData": "unpack_results" }
        ]
    })
});