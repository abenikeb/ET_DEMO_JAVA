const baseUrl = "http://localhost:8080";
const appId = "853694808089602"; // need replace with real appid.
let h5Token = "";
let rawRequest = "";

// this token just for test, the real token please call applyTokenFromApp to get when the page open in app.
function applyH5Token() {
  fetch(baseUrl + "/apply/h5token", {
    method: "post",
    headers: {
      "Content-Type": "application/json",
    },
    body: "{}",
  }).then((res) => {
    res.json().then((json) => {
      console.log(json);
      let biz = json.biz_content;
      h5Token = biz.access_token;
      document.getElementById(
        "div_h5_token"
      ).innerHTML = `access token: <br> <span style='color:red;'>${biz.access_token}</span>`;
    });
  });
}

function applyTokenFromApp() {
  window.xm.native("getMiniAppToken", { appId, appId }).then((res) => {
    h5Token = JSON.parse(res).token;
    document.getElementById(
      "div_h5_token_from_app"
    ).innerHTML = `token: <br> <span style='color:red;'>${h5Token}</span>`;
  });
}

function authToken() {
  if (!h5Token) {
    alert("please apply H5 token frist.");
    return;
  }
  fetch(baseUrl + "/auth/token", {
    method: "post",
    headers: {
      "Content-Type": "application/json",
      "mode":"cors"
    },
    body: JSON.stringify({
      authToken: h5Token,
    }),
  }).then((res) => {
    res.json().then((json) => {
      console.log(json);
      let biz = json.biz_content;
      let openId = biz.open_id;
      let identityId = biz.identityId;
      console.log(openId);
      document.getElementById(
        "div_userinfo"
      ).innerHTML = `openId: <br> <span style='color:red;'>${openId}</span>
                <br>
                identityId: <br> <span style='color:red;'>${identityId}</span>`;
    });
  });
}

function createOrder() {
  let title = document.getElementById("txtTitle").value;
  let amount = document.getElementById("txtAmount").value;
  if (title == "" || amount == "") {
    alert("please input order info");
    return;
  }
  fetch(baseUrl + "/create/order", {
    method: "post",
    headers: {
      "Content-Type": "application/json",
      "mode":"cors"
    },
    body: JSON.stringify({
      title: title,
      amount: amount,
    }),
  }).then((res) => {
    res.text().then((json) => {
      rawRequest = json;
      document.getElementById(
        "div_orderinfo"
      ).innerHTML = `rawRequest: <br/><span style='color:red;'>${json}</span>`;
    });
  });
}

function startPay() {
  if (!rawRequest) {
    alert("rawRequest is null");
    return;
  }
  if (!window.customerApp) {
    alert("This page is not in app!")
  }
  window.xm.native("startPay", { rawRequest, rawRequest }).then((res) => {
    alert("payment success!");
  });
}
