var CryptoJSAesJson = {
	stringify: function(cipherParams) {
		var j = { ct: cipherParams.ciphertext.toString(CryptoJS.enc.Base64) };
		if (cipherParams.iv) j.iv = cipherParams.iv.toString();
		if (cipherParams.salt) j.s = cipherParams.salt.toString();
		return JSON.stringify(j);
	}
}

function updateClock() {
    var clockElement = document.getElementById("clock");
    if (clockElement) {
        var now = new Date();
        var hours = now.getHours();
        var minutes = now.getMinutes();
        
        var ampm = hours >= 12 ? "PM" : "AM";
        
        hours = hours % 12;
        hours = hours ? hours : 12;
        minutes = minutes < 10 ? "0" + minutes : minutes;
        
        var timeString = hours + ":" + minutes + " " + ampm;
        clockElement.textContent = timeString;
    }
}

document.addEventListener("DOMContentLoaded", function() {
    updateClock();
    setInterval(updateClock, 1000);
});

function openPasswordPopup() {
    var url = "https://rms.webseal.iamapp.com/home/myPwd/myPwdChange.jsp";
    var name = "비밀번호 변경";
    var width = 1000;
    var height = 700;
    
    var left = (window.screen.width / 2) - (width / 2);
    var top = (window.screen.height / 2) - (height / 2);
    
    var options = 'width=' + width + ', height=' + height + ', top=' + top + ', left=' + left + ', resizable=no, scrollbars=yes, status=no, titlebar=no';
    
    window.open(url, name, options);
}

function login() {
    Swal.fire({
        title: '',
        allowOutsideClick: false,
        showConfirmButton: false,
        background: 'transparent',
        didOpen: () => {
            Swal.showLoading();
        }
    });

    axios({
        method: 'POST',
        url: context_root + '/login',
        data: {
            user_id: CryptoJS.AES.encrypt(USER_ID, 'precursorEncryptKey', { format: CryptoJSAesJson }).toString()
        }
    })
    .then(function(response) {
        Swal.close();
        if(response.data.success) {
            location.href = response.data.url; 
        } else {
            Swal.fire('로그인 실패', response.data.message, 'error');
        }
    })
    .catch(function(error) {
        Swal.close();
        Swal.fire({
            title: '오류',
            html: error.message || '데이터 전송 실패',
            icon: 'error',
            confirmButtonColor: '#0096FF'
        });
    });
}

function logout() {
    Swal.fire({
        title: '',
        allowOutsideClick: false,
        showConfirmButton: false,
        background: 'transparent',
        didOpen: () => {
            Swal.showLoading();
        }
    });

    axios({
        method: 'POST',
        url: context_root + '/logout'
    })
    .then(function(response) {
        Swal.close();
        if(response.data.success) {
            $('body').append(response.data.url);
        } else {
            Swal.fire('로그아웃 실패', response.data.message, 'error');
        }
    })
    .catch(function(error) {
        Swal.close();
        Swal.fire({
            title: '오류',
            html: error.message || '데이터 전송 실패',
            icon: 'error',
            confirmButtonColor: '#0096FF'
        });
    });
}