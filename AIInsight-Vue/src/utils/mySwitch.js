import { ref } from 'vue'
let flag = ref(true)
const mySwitch = () => {
    const pre_box = document.querySelector('.pre-box')
    const img = document.querySelector("#avatar")
    if (flag.value) {
        pre_box.style.transform = "translateX(100%)"
        pre_box.style.backgroundColor = "#abd9f1"
        img.src = require("@/assets/img/login2.jpg")
    }
    else {
        pre_box.style.transform = "translateX(0%)"
        pre_box.style.backgroundColor = "#d9f9cd"
        img.src = require("@/assets/img/login1.jpg")
    }
    flag.value = !flag.value
}
export default mySwitch