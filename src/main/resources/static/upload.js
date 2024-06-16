
function _(el) {
    return document.getElementById(el);
}
let file;

//getting attribute values in a json object
const dig = (obj, target) =>
    target in obj
        ? obj[target]
        : Object.values(obj).reduce((acc, val) => {
            if (acc !== undefined) return acc;
            if (typeof val === 'object') return dig(val, target);
        }, undefined);
function Register(){
    _("registernmsg").innerHTML="";
    _("email_msg").innerHTML="";
    let pass1=_("pass1").value;
    let pass2=_("pass2").value;
    let email=_("email").value;
    let fname=_("fname").value;
    let lname=_("lname").value;
    if(pass1 == "") {
        document.getElementById("message").innerHTML = "Fill the password please!";
        return false;
    }
    if(pass1.length < 6) {
        document.getElementById("message").innerHTML = "Password length must be atleast 6 characters";
        return false;
    }
    if(pass1.length > 15) {
        document.getElementById("message").innerHTML = "Password length must not exceed 15 characters";
        return false;
    }
    if (pass1!==pass2){
        document.getElementById("message").innerHTML = "Password and Confirm password don't match";
        return false;
    }
    else {
        console.log("waiting for verification");
    }

    const formdata = new FormData();
    formdata.append("firstname",fname );
    formdata.append("lastname",lname );
    formdata.append("email", email.toLowerCase());
    formdata.append("password",pass1 );
    let ajax = new XMLHttpRequest();
    ajax.upload.addEventListener("progress", registerprogressHandler, false);
    ajax.addEventListener("load", registercompleteHandler, false);
    ajax.addEventListener("error", registererrorHandler, false);
    ajax.addEventListener("abort", registerabortHandler, false);
    ajax.open("POST", "/registration/register");
    ajax.send(formdata);
    function registerprogressHandler(event)
    {
        console.log(event.target.responseText);
        _("registernmsg").innerHTML="Registering, please wait...";
    }
    function registercompleteHandler(event) {
        console.log(event.target.responseText);
        console.log(event.target.status)
        if (event.target.status==200) {
            if (event.target.responseText=="Email already taken") {
                _("registernmsg").innerText = "";
               return _("email_msg").innerText = "Email already taken";
            }
             else
            {
                // Swal.fire({
                //     position: 'top-end',
                //     icon: 'success',
                //     title: 'Registration successful',
                //     showConfirmButton: false,
                //     timer: 1500
                // })
                _("registernmsg").innerText = "Registration successful!!...";
               return window.location.href = "/registersuccess";
            }
        }else {
           return _("email_msg").innerText = "Registration Failed, Please Try Again";
        }

    }

    function registererrorHandler(event) {
        console.log(event.target.responseText);
        _("registernmsg").innerText="Error occurred, try again";
    }

    function registerabortHandler(event) {
        console.log(event.target.responseText);
        _("registernmsg").innerText="process aborted";
    }
}
function uploadFile() {
    let auth=_("isauth");
    if(auth==null){
        alert("Please login first to access the scanning service");
       return  window.location.href="/signing";
    }
    file = _("file1").files[0];
    _("progressBar").style.display="block";
    _("taskidtext").style.display="none";
    _("status1").style.color="rgba(52,52,52,0.94)"
    const cb1 =_("cscan");
    const cb2 =_("unpack");
    const cb3 =_("share");
    let check1;
    let check2;
    let check3;
    if (cb1.checked){
        check1=1;
    }else {
        check1=0;
    }
    if (cb2.checked){
        check2=1;
    }else{
        check2=0;
    }
    if (cb3.checked){
        check3=1;
    }else{
        check3=0;
    }
    let formdata = new FormData();
    let ajax = new XMLHttpRequest();
    formdata.append("file", file);
    formdata.append("clamav_s",check3);
    formdata.append("share_f",check1);
    formdata.append("unpack_f",check2);

    ajax.upload.addEventListener("progress", progressHandler, false);
    ajax.addEventListener("load", completeHandler, false);
    ajax.addEventListener("error", incompleteHandler, false);
    ajax.addEventListener("error", errorHandler, false);
    ajax.addEventListener("abort", abortHandler, false);
    ajax.open("POST", "/upload");
    ajax.send(formdata);
}
function progressHandler(event) {
    console.log(event.target.responseText);
    _("loaded_n_total").innerHTML = "Uploaded " + Math.round(event.loaded/1024).toFixed(1) + " kilobytes of " + Math.round(event.total/1024).toFixed(1);
    let percent = (event.loaded / event.total) * 100;
    _("progressBar").value = Math.round(percent);
    _("status1").innerHTML = Math.round(percent) + "% uploaded... please wait";
}

function completeHandler(event) {
   const response = JSON.parse(event.target.responseText);
    let  taskid= (dig(response, "task_id"));
    _("success").style.display = "block";
    _("success").innerHTML="File uploaded successfully. Please wait as we Query your results";
    _("status1").style.color = "#30a903";
    _("status1").innerHTML ="File Upload Complete";
    _("progressBar").value = 0;
    _("progressBar").style.display="none";
    _("taskidtext").style.display="block";
    _("taskid").style.Color="#577c5c";
    _("taskid").innerHTML=taskid;
    _("taskidtext").style.display="block";

    if (event.target.status==200) {
        _("success").style.display="none";
        queryResult();
    }else
        incompleteHandler(event);

}
function incompleteHandler(event) {
    _("status1").style.color="rgba(246,122,117,0.88)"
    _("status1").innerHTML = "Could not complete the process ".concat(event.target.responseText);
}
function errorHandler(event) {
    _("progressBar").style.display="none";
    _("status1").style.color="rgba(211,108,104,0.88)"
    // let response = event.target.responseText;
    // console.log(response);
    // _("status1").innerHTML=response;
    _("status1").innerHTML = "Upload Failed! " +'<br/>'+ Math.round((file.size / 1024))+" mb is more than Max size allowed";
}

function abortHandler(event) {
    _("progressBar").style.display="none";
    _("status1").style.color="rgba(246,122,117,0.88)"
    _("status1").innerHTML = "Upload Aborted";
}

function queryResult() {
    _("result").innerHTML = "Querying status, please wait...";
    _("sp").style.display="block";
    _("resulttabs").style.display="none";
    _("querybtn").style.display="none";

   // let myJSON = JSON. stringify(response);
    let ajax = new XMLHttpRequest();
    ajax.upload.addEventListener("progress", progressQuery, false);
    ajax.addEventListener("load", completeQuery, false);
    ajax.addEventListener("error", errorQuery, false);
    ajax.addEventListener("abort", abortQuery, false);
    ajax.open("POST", "/query",true);
    ajax.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
            _("result").innerHTML= this.responseText;
        }else
        _("result").innerHTML= this.responseText;
        _("sp").style.display="none";
    };
    ajax.send();
}

function progressQuery(queryevent) {
    _("result").innerHTML='';
    _("status").innerHTML='';
    _("querybtn").style.display="none";
    _("result").innerHTML = "Querying status, please wait..";
    _("sp").style.display="block";
    _("status").innerHTML="Status: Queued";
    _("status").style.color = "#17A2B8"
}

function completeQuery(queryevent) {

    _("result").innerHTML='';
   _("status").innerHTML='';
    _("querybtn").style.display = "block";
    _("querybtn").innerHTML = "Querying Status...";
    _('success').style.display = "none";
    _('resultdisplay').style.backgroundColor = "#f5f5f5";
    _('resultdisplay').style.Color = "black";
    _("querybtn").style.backgroundColor = "#bfbfbf";
    _("querybtn").style.border = "none";
    let text = queryevent.target.responseText;

    if (text == "Queued") {
        _("querybtn").style.display = "block";
        _("result").innerHTML='';
        _("querybtn").innerHTML='';
        _("result").innerHTML="Status: Queued";
        _("querybtn").innerHTML = 'Queued...';
        _("result").style.color = "#d4a404";
         queryResult();
    }else if (text == "malicious not found") {
    _("result").innerHTML='';
        _("querybtn").innerHTML='';
        _("querybtn").style.display = "none";
        _("result").innerHTML="The File Is Safe. No Malicious Found in the file by the scanner";
        _("result").style.color = "#30a903";
        _("sp").style.display="none";
        filltabs();
        _("resulttabs").style.display="block";
    } else if (text == "Malicious found") {
        // let text = queryevent.target.responseText;
        _("result").innerHTML='';
        _("querybtn").innerHTML='';
        _("querybtn").style.display = "none";
        _("result").innerHTML="This file is considered unsafe by the ClamAv scanning engine. View more details in Detection tab below.";
        _("result").style.color = "#cc6361";
        _("sp").style.display="none";
       filltabs();
        _("resulttabs").style.display="block";
    } else if(queryevent.target.status==500) {
        _("result").innerHTML='';
        _("querybtn").innerHTML='';
        _("result").innerHTML="Connection Timeout";
        _("querybtn").disabled = false;
        _("querybtn").style.backgroundColor="#17A2B8";
        _("querybtn").innerHTML = 'Try again';
       _("sp").style.display="none";
    }
    else{
        _("result").innerHTML='';
        _("querybtn").innerHTML='';
        _("result").innerHTML=queryevent.target.responseText.status;
        _("querybtn").disabled = false;
        _("querybtn").innerHTML = 'Try again';
        _("sp").style.display="none";
        }
}

function errorQuery(queryevent) {
    _("result").style.color="rgba(238,119,113,0.88)"
    let text = queryevent.target.responseText;
    _("result").innerHTML='';
    _("querybtn").innerHTML='';
    _("result").innerHTML=text;
    _("querybtn").disabled = false;
    _("querybtn").innerHTML = 'Try again';
    _("sp").style.display="none";
}

function abortQuery(queryevent) {
    _("result").style.color="rgba(243,113,108,0.88)"
    _("result").innerHTML = "Upload Aborted";
}

// async function uploadFile(){
//
//     let btnupload = document.getElementById('upload-button');
//     btnupload.style.backgroundColor = "#009578";
//    btnupload.innerHTML = 'Checking File...';
//         let files = document.getElementById('fileupload').files;
//     if(files.length==0){
//        btnupload.innerHTML = 'Scan File';
//         document.getElementById("alert").style.display='block';
//           return;
//     }else {
//         //validatefile();
//     }
//
//     let filenames = "";
//     for(let i=0; i<files.length; i++){
//         filenames+=files[i].name+"\n";
//     }
//     document.getElementById("alert").style.display='none';
//     //document.getElementById('myProgress').style.display = 'block';
//     let l1 = document.getElementById("cscan").value;
//     let l2 = document.getElementById("unpack").value;
//     let l3 = document.getElementById("share").value;
//     let formData = new FormData();
//     formData.append("file", fileupload.files[0]);
//      // formData.append("cscan",fileupload.val(l1));
//      // formData.append("unpack", fileuploadl.val(l2));
//      // formData.append("share", fileupload.val(l3));
//     btnupload.innerHTML = 'Uploading File...';
// try {
//
//     let response= await fetch("/upload", {
//         method: "POST",
//         body: formData
//     });
//
//     document.cookie = "filter=value";
//         let reslt = document.getElementById("result");
//         let success = document.getElementById('success');
//         if (response.status === 200) {
//             success.style.display = "block";
//             btnupload.innerHTML = 'File Upload Complete';
//             success.innerHTML="File uploaded successfully. Please wait as we Query your results";
//             //spinner.style.display="none";
//             await queryResult();
//         } else {
//             //spinner.style.display="none";
//             document.getElementById('success').style.display = "none";
//             btnupload.innerHTML = 'unable to Upload File...';
//             reslt.innerHTML="Error "+await response.text()+" Try again";
//         }
// }catch (e) {
//     //spinner.style.display="none";
//     document.getElementById('success').style.display = "none";
//     btnupload.style.backgroundColor = "#f8261e";
//     btnupload.innerHTML = 'Error occurred';
//     document.getElementById("result").append(e)
// }
// }

async function queryResult1() {

    let reslt = document.getElementById("result");
    let querybtn = document.getElementById("querybtn");
querybtn.style.display="none";
    reslt.innerHTML = "Querying status, please wait..";
    document.getElementById("sp").style.display="block";
    document.getElementById("status").innerHTML="Status: Queued";
    document.getElementById("status").style.color = "#17A2B8"
    let response = await fetch('/query', {
        method: "POST",
        body: "application/json"
    }).then((res) => {
        return res.text();
    }).then((text) => {
        reslt.innerHTML='';
        document.getElementById("status").innerHTML='';
        querybtn.style.display = "block";
        reslt.innerHTML = querybtn.innerHTML = "Querying Status...";
        document.getElementById('success').style.display = "none";
        document.getElementById('resultdisplay').style.backgroundColor = "#bfbfbf";
        document.getElementById('resultdisplay').style.Color = "black";
       querybtn.style.backgroundColor = "#bfbfbf";
       querybtn.style.border = "none";
        if (text == "Queued") {
            reslt.innerHTML='';
            querybtn.innerHTML='';
            reslt.innerHTML="Status: Queued...";
            querybtn.innerHTML = 'Queued...';
            reslt.style.color = "#d4a404";
            // queryResult();
        }else if (text == "malicious not found") {
            reslt.innerHTML='';
            querybtn.innerHTML='';
            document.getElementById("querybtn").style.display = "none";
            reslt.append("The File Is Safe. No Malicious Found in the file by the scanner");
            reslt.style.color = "#30a903";
            document.getElementById("sp").style.display="none";
        } else if (text == "malicious found") {
            reslt.innerHTML='';
            querybtn.innerHTML='';
            document.getElementById("querybtn").style.display = "none";
            reslt.append("The File Is Not Safe, Malicious Found!!");
            reslt.style.color = "#f8261e";
            document.getElementById("sp").style.display="none";
        } else {
            reslt.innerHTML='';
            querybtn.innerHTML='';
            reslt.innerHTML="Connection Time Out";
            querybtn.disabled = false;
            querybtn.innerHTML = 'Try again';
            document.getElementById("sp").style.display="none";
        }

    });
}

    function validatefile() {
        Filevalidation = () => {
            const fi = document.getElementById('fileupload');
            // Check if any file is selected.
            if (fi.files.length > 0) {
                let i;
                for (i = 0; i <= fi.files.length - 1; i++) {

                    let fsize = fi.files.item(i).size;
                    let file = Math.round((fsize / 1024));
                    // The size of the file.
                    if (file >= 204800) {
                        document.getElementById('size').innerHTML = '<b>' +
                            "File too Big, please select a file less than 200mb" + '</b>';
                        alert(fsize +"max size is 200mbs");
                    } else if (file < 20) {
                        alert(fsize+" min size is 2kbs");
                        document.getElementById('size').innerHTML = '<b>' +
                            "File too small, please select a file greater than 2kb" + '</b>';
                    } else {
                        alert(fsize+ " your size is okay");
                        document.getElementById('size').innerHTML = '<b>'
                            + file + '</b> KB';
                    }
                }
            }
        }
    }

function previousuploadedfiles() {
window.location.href="/previousScannedFiles";
        // let response = await fetch("registration/previousScannedFiles", {
        //     method: "GET"
        // });
}

 function filltabs(){
     let ajax = new XMLHttpRequest();
     ajax.upload.addEventListener("progress", progressfillingtab, false);
     ajax.addEventListener("load", completefillingtab, false);
     ajax.addEventListener("error", errorfillingtab, false);
     ajax.addEventListener("abort", abortfillingtab, false);
     ajax.open("GET", "/fillingtabs");
     ajax.send();
     function progressfillingtab(event) {
         console.log(event.target.responseText);
     }
     function completefillingtab(event) {
         const response = JSON.parse(event.target.responseText);
         let isClamav=(dig(response,"clamav_results"));
         let isStatic=(dig(response,"static_results"));
         let isUnpack=(dig(response,"unpack_results"));
         if (isClamav.length===0){
             _("clamav").append("Nothing Found");
         }else {
             _("clamav").append(JSON.stringify(isClamav));
         }
          if(isStatic.length===0)
         {
             _("static").append("Nothing Found");
         }else {
              _("static").append(JSON.stringify(isStatic));
          }
          if(isUnpack.length===0)
         {
             _("unpac").append("Nothing Found");
         }else {
              _("unpac").append(JSON.stringify(isUnpack));
          }
     }

     function errorfillingtab(event) {
         console.log(event.target.responseText);

     }

     function abortfillingtab(event) {
         console.log(event.target.responseText);
     }

}

function verifyPassword() {
    let pw = document.getElementById("pass1").value;
    let pwc = document.getElementById("pass2").value;
    //check empty password field
    if(pw == "") {
        document.getElementById("message").innerHTML = "Fill the password please!";
        return false;
    }
    if(pw.length < 6) {
        document.getElementById("message").innerHTML = "Password length must be atleast 6 characters";
        return false;
    }
    if(pw.length > 15) {
        document.getElementById("message").innerHTML = "Password length must not exceed 15 characters";
        return false;
    }
    if (pw!==pwc){
        document.getElementById("message").innerHTML = "Password and Confirm password don't match";
        return false;
    }
    else {
      console.log("Password is correct");
    }
}