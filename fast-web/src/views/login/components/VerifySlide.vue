<template>
  <div style="position: relative;">
    <div
      v-if="type === '2'"
      class="verify-img-out"
      :style="{height: (parseInt(setSize.imgHeight) + vSpace) + 'px'}"
    >
      <div
        class="verify-img-panel"
        :style="{width: setSize.imgWidth,
                 height: setSize.imgHeight,}"
      >
        <img
          :src="'data:image/png;base64,'+backImgBase"
          alt=""
          style="width:100%;height:100%;display:block"
        >
        <div
          v-show="showRefresh"
          class="verify-refresh"
          @click="refresh"
        >
          <i class="iconfont icon-refresh" />
        </div>
        <transition name="tips">
          <span
            v-if="tipWords"
            class="verify-tips"
            :class="passFlag ?'suc-bg':'err-bg'"
          >{{ tipWords }}</span>
        </transition>
      </div>
    </div>
    <!-- 公共部分 -->
    <div
      class="verify-bar-area"
      :style="{width: setSize.imgWidth,
               height: barSize.height,
               'line-height':barSize.height}"
    >
      <span
        class="verify-msg"
        v-text="text"
      />
      <div
        class="verify-left-bar"
        :style="{width: (leftBarWidth!==undefined)?leftBarWidth: (subBlockWidth - 2) + 'px', height: barSize.height, 'border-color': leftBarBorderColor, 'background-color': leftBarBackgroundColor, 'border-radius': leftBarBorderRadius, transaction: transitionWidth}"
      >
        <span
          class="verify-msg"
          v-text="finishText"
        />
        <div
          class="verify-move-block"
          :style="{width: subBlockWidth + 'px', height: barSize.height, 'background-color': moveBlockBackgroundColor, 'border-color': moveBlockBorderColor, 'border-radius': moveBlockBorderRadius, left: moveBlockLeft, transition: transitionLeft}"
          @touchstart="start"
          @mousedown="start"
        >
          <i
            :class="['verify-icon iconfont', iconClass]"
            :style="{color: iconColor}"
          />
          <div
            v-if="type === '2'"
            class="verify-sub-block"
            :style="{'width': subBlockWidth + 'px',
                     'height': setSize.imgHeight,
                     'top':'-' + (parseInt(setSize.imgHeight) + vSpace) + 'px',
                     'background-size': setSize.imgWidth + ' ' + setSize.imgHeight,
            }"
          >
            <img
              :src="'data:image/png;base64,'+blockBackImgBase"
              alt=""
              style="width:100%;height:100%;display:block;-webkit-user-drag:none;"
            >
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script type="text/babel">
/**
 * VerifySlide
 * @description 滑块
 * */
import {aesEncrypt} from "@/utils/ase"
import {resetSize} from '@/utils/util'
import {reqCheck, reqGet} from "@/api/captcha"
import {computed, getCurrentInstance, nextTick, onMounted, reactive, ref, toRefs, watch} from 'vue';
//  "captchaType":"blockPuzzle",
export default {
  name: 'VerifySlide',
  props: {
    captchaType: {
      type: String,
    },
    type: {
      type: String,
      default: '1'
    },
    //弹出式pop，固定fixed
    mode: {
      type: String,
      default: 'fixed'
    },
    vSpace: {
      type: Number,
      default: 5
    },
    explain: {
      type: String,
      default: '向右滑动完成验证'
    },
    imgSize: {
      type: Object,
      default() {
        return {
          width: '310px',
          height: '155px'
        }
      }
    },
    blockSize: {
      type: Object,
      default() {
        return {
          width: '50px',
          height: '50px'
        }
      }
    },
    barSize: {
      type: Object,
      default() {
        return {
          width: '310px',
          height: '40px'
        }
      }
    }
  },
  setup(props, context) {
    const {mode, captchaType, vSpace, imgSize, barSize, type, blockSize, explain} = toRefs(props)
    const {proxy} = getCurrentInstance();
    const secretKey = ref(''),           //后端返回的ase加密秘钥
        passFlag = ref(''),         //是否通过的标识
        backImgBase = ref(''),      //验证码背景图片
        blockBackImgBase = ref(''), //验证滑块的背景图片
        backToken = ref(''),        //后端返回的唯一token值
        startMoveTime = ref(''),    //移动开始的时间
        endMovetime = ref(''),      //移动结束的时间
        tipsBackColor = ref(''),    //提示词的背景颜色
        tipWords = ref(''),
        text = ref(''),
        finishText = ref(''),
        setSize = reactive({
          imgHeight: '155px',
          imgWidth: '310px',
          barHeight: '40px',
          barWidth: '310px'
        }),
        top = ref(0),
        left = ref(0),
        moveBlockLeft = ref(undefined),
        leftBarWidth = ref(undefined),
        // 移动中样式
        moveBlockBackgroundColor = ref(undefined),
        moveBlockBorderColor = ref(undefined),
        moveBlockBorderRadius = ref(undefined),
        leftBarBackgroundColor = ref('transparent'),
        leftBarBorderColor = ref('transparent'),
        leftBarBorderRadius = ref('8px'),
        iconColor = ref(undefined),
        iconClass = ref('icon-right'),
        status = ref(false),	    //鼠标状态
        isEnd = ref(false),		//是够验证完成
        showRefresh = ref(true),
        transitionLeft = ref(''),
        transitionWidth = ref(''),
        startLeft = ref(0)

    const barArea = computed(() => {
      return proxy.$el ? proxy.$el.querySelector('.verify-bar-area') : null
    })

    const moveBlock = computed(() => {
      return proxy.$el ? proxy.$el.querySelector('.verify-move-block') : null
    })

    // 缺口宽度（310px 图片对应 47px）
    const subBlockWidth = computed(() => {
      const imgWidth = parseInt(setSize.imgWidth)
      return Math.floor(imgWidth * 47 / 310)
    })

    function init() {
      text.value = explain.value
      getPictrue();
      nextTick(() => {
        const {imgHeight, imgWidth, barHeight, barWidth} = resetSize(proxy)
        setSize.imgHeight = imgHeight
        setSize.imgWidth = imgWidth
        setSize.barHeight = barHeight
        setSize.barWidth = barWidth
        proxy.$parent.$emit('ready', proxy)
      })

      window.removeEventListener("touchmove", function (e) {
        move(e);
      });
      window.removeEventListener("mousemove", function (e) {
        move(e);
      });

      //鼠标松开
      window.removeEventListener("touchend", function () {
        end();
      });
      window.removeEventListener("mouseup", function () {
        end();
      });

      window.addEventListener("touchmove", function (e) {
        move(e);
      });
      window.addEventListener("mousemove", function (e) {
        move(e);
      });

      //鼠标松开
      window.addEventListener("touchend", function () {
        end();
      });
      window.addEventListener("mouseup", function () {
        end();
      });
    }

    watch(type, () => {
      init()
    })
    onMounted(() => {
      // 禁止拖拽
      init()
      proxy.$el.onselectstart = function () {
        return false
      }
    })

    //鼠标按下
    function start(e) {
      e = e || window.event
      let x;
      if (!e.touches) {  //兼容PC端
        x = e.clientX;
      } else {           //兼容移动端
        x = e.touches[0].pageX;
      }
      // 点击时鼠标相对于 barArea border box 左边缘的位置
      // 后续计算时会减去边框宽度得到相对于 content box 的位置
      startLeft.value = Math.floor(x - barArea.value.getBoundingClientRect().left);
      startMoveTime.value = +new Date();    //开始滑动的时间
      if (isEnd.value == false) {
        text.value = ''
        // 设置滑块初始位置为 0（视觉位置）
        moveBlockLeft.value = '0px'
        // leftBarWidth CSS width = 滑块宽度 - 2px 边框
        leftBarWidth.value = (subBlockWidth.value - 2) + 'px'
        moveBlockBackgroundColor.value = 'var(--color-primary)'
        moveBlockBorderColor.value = 'var(--color-primary)'
        // 滑块在最左边时四角圆角
        moveBlockBorderRadius.value = '8px'
        leftBarBackgroundColor.value = 'var(--color-primary-lighter)'
        leftBarBorderColor.value = 'var(--color-primary)'
        // 滑轨背景在最左边时四角圆角
        leftBarBorderRadius.value = '8px'
        iconColor.value = '#fff'
        e.stopPropagation();
        status.value = true;
      }
    }

    //鼠标移动
    function move(e) {
      e = e || window.event
      if (status.value && isEnd.value == false) {
        let x;
        if (!e.touches) {  //兼容PC端
          x = e.clientX;
        } else {           //兼容移动端
          x = e.touches[0].pageX;
        }
        const barAreaLeft = barArea.value.getBoundingClientRect().left;
        // 使用图片宽度（与 barArea CSS width 一致，不含 border）
        const barWidth = parseInt(setSize.imgWidth);
        const blockWidth = subBlockWidth.value;

        // 鼠标相对于 barArea content box 左边缘的位置
        // getBoundingClientRect 返回 border box，需要减去边框
        const barAreaBorder = 1;
        const mouseLeft = x - barAreaLeft - barAreaBorder

        // 滑块视觉位置 = 鼠标移动距离
        // 初始时鼠标位置 = startLeft - barAreaBorder（相对于 content box）
        let visualLeft = mouseLeft - (startLeft.value - barAreaBorder)

        // 左边界
        if (visualLeft < 0) {
          visualLeft = 0
        }
        // 右边界：滑块右边 <= barWidth
        if (visualLeft > barWidth - blockWidth) {
          visualLeft = barWidth - blockWidth
        }

        // 设置滑块 CSS left（直接使用视觉位置）
        moveBlockLeft.value = visualLeft + "px"
        // leftBarWidth CSS width = 视觉宽度 - 2px 边框
        leftBarWidth.value = (visualLeft + blockWidth - 2) + "px"
        // 圆角动态切换
        if (visualLeft < 1) {
          // 最左边：两者四角圆角
          moveBlockBorderRadius.value = '8px'
          leftBarBorderRadius.value = '8px'
        } else {
          // 移动中：两者右上右下圆角
          moveBlockBorderRadius.value = '0 8px 8px 0'
          leftBarBorderRadius.value = '0 8px 8px 0'
        }
      }
    }

    //鼠标松开
    function end() {
      endMovetime.value = +new Date();
      //判断是否重合
      if (status.value && isEnd.value == false) {
        // 滑块视觉位置（直接从 CSS left 获取）
        const visualLeft = parseInt((moveBlockLeft.value || '0px').replace('px', ''))
        // 转换为标准 310px 图片的坐标
        const moveLeftDistance = visualLeft * 310 / parseInt(setSize.imgWidth)
        const data = {
          captchaType: captchaType.value,
          "pointJson": secretKey.value ? aesEncrypt(JSON.stringify({
            x: moveLeftDistance,
            y: 5.0
          }), secretKey.value) : JSON.stringify({x: moveLeftDistance, y: 5.0}),
          "token": backToken.value
        }
        reqCheck(data).then(res => {
          if (res.repCode == "0000") {
            moveBlockBackgroundColor.value = 'var(--color-success)'
            moveBlockBorderColor.value = 'var(--color-success)'
            moveBlockBorderRadius.value = '0 8px 8px 0'
            leftBarBackgroundColor.value = 'var(--color-success-lighter)'
            leftBarBorderColor.value = 'var(--color-success)'
            leftBarBorderRadius.value = '0 8px 8px 0'
            iconColor.value = '#fff'
            iconClass.value = 'icon-check'
            showRefresh.value = false
            isEnd.value = true;
            passFlag.value = true
            tipWords.value = `${((endMovetime.value - startMoveTime.value) / 1000).toFixed(2)}s验证成功`
            const captchaVerification = secretKey.value ? aesEncrypt(backToken.value + '---' + JSON.stringify({
              x: moveLeftDistance,
              y: 5.0
            }), secretKey.value) : backToken.value + '---' + JSON.stringify({x: moveLeftDistance, y: 5.0})
            setTimeout(() => {
              tipWords.value = ""
              proxy.$parent.closeBox();
              proxy.$parent.$emit('success', {captchaVerification})
            }, 1000)
          } else {
            moveBlockBackgroundColor.value = 'var(--color-danger)'
            moveBlockBorderColor.value = 'var(--color-danger)'
            moveBlockBorderRadius.value = '0 8px 8px 0'
            leftBarBackgroundColor.value = 'var(--color-danger-lighter)'
            leftBarBorderColor.value = 'var(--color-danger)'
            leftBarBorderRadius.value = '0 8px 8px 0'
            iconColor.value = '#fff'
            iconClass.value = 'icon-close'
            passFlag.value = false
            setTimeout(function () {
              refresh();
            }, 1000);
            proxy.$parent.$emit('error', proxy)
            tipWords.value = "验证失败"
            setTimeout(() => {
              tipWords.value = ""
            }, 1000)
          }
        })
        status.value = false;
      }
    }

    const refresh = () => {
      showRefresh.value = true
      finishText.value = ''

      transitionLeft.value = 'left .3s'
      moveBlockLeft.value = 0

      leftBarWidth.value = undefined
      transitionWidth.value = 'width .3s'

      leftBarBackgroundColor.value = 'transparent'
      leftBarBorderColor.value = 'transparent'
      // 刷新后滑块在最左边，四角圆角
      leftBarBorderRadius.value = '8px'
      moveBlockBackgroundColor.value = 'var(--color-primary-lighter)'
      moveBlockBorderColor.value = 'transparent'
      moveBlockBorderRadius.value = '8px'
      iconColor.value = 'var(--color-foreground)'
      iconClass.value = 'icon-right'
      isEnd.value = false

      getPictrue()
      setTimeout(() => {
        transitionWidth.value = ''
        transitionLeft.value = ''
        text.value = explain.value
      }, 300)
    }

    // 请求背景图片和验证图片
    function getPictrue() {
      const data = {
        captchaType: captchaType.value
      }
      reqGet(data).then(res => {
        if (res.repCode == "0000") {
          backImgBase.value = res.repData.originalImageBase64
          blockBackImgBase.value = res.repData.jigsawImageBase64
          backToken.value = res.repData.token
          secretKey.value = res.repData.secretKey
        } else {
          tipWords.value = res.repMsg;
        }
      })
    }

    return {
      secretKey,           //后端返回的ase加密秘钥
      passFlag,         //是否通过的标识
      backImgBase,      //验证码背景图片
      blockBackImgBase, //验证滑块的背景图片
      backToken,        //后端返回的唯一token值
      startMoveTime,    //移动开始的时间
      endMovetime,      //移动结束的时间
      tipsBackColor,    //提示词的背景颜色
      tipWords,
      text,
      finishText,
      setSize,
      subBlockWidth,    //缺口宽度（滑块宽度）
      top,
      left,
      moveBlockLeft,
      leftBarWidth,
      // 移动中样式
      moveBlockBackgroundColor,
      moveBlockBorderColor,
      moveBlockBorderRadius,
      leftBarBackgroundColor,
      leftBarBorderColor,
      leftBarBorderRadius,
      iconColor,
      iconClass,
      status,	    //鼠标状态
      isEnd,		//是够验证完成
      showRefresh,
      transitionLeft,
      transitionWidth,
      barArea,
      moveBlock,
      refresh,
      start
    }
  },
}
</script>

