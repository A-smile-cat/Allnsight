<template>
<el-menu
    style="width: 100%;height:100%; min-height: calc(100vh - 50px)"
    :default-active="currentPath"
    class="el-menu-vertical-demo"
    router
    mode="vertical"
    background-color="#30333c"
    text-color="#fff"
    active-text-color="#ffd04b"
    :collapse="isCollapse"
>
    <div style="height: 80px;"></div>
    <!-- index 没有用但必需字段 -->
    <el-sub-menu
    v-for="(item, i) in filteredMenus"
    :key="i"
    :index="i.toString()"
    style="text-align: left"
    >
    <template #title>
        <el-icon>
          <component :is="iconMap[item.iconCls]" />
        </el-icon>
        <span style="font-size: 17px;">{{ item.name_des }}</span>
    </template>
    <el-menu-item
        v-for="child in item.children"
        :key="child.path"
        :index="child.path"
      >
        <el-icon>
          <component :is="iconMap[child.iconCls]" />
        </el-icon>
        <span>{{ child.name_des }}</span>
    </el-menu-item>
    </el-sub-menu>

    <el-menu-item
    v-for="(item, i) in singleMenus"
    :key=i
    :index="item.path"
    style="text-align: left"
  >
    <el-icon>
      <component :is="iconMap[item.iconCls]" />
    </el-icon>
    <span style="font-size: 17px">{{ item.name_des }}</span>
  </el-menu-item>
</el-menu>
</template>
  
<script setup>
import { computed, ref } from 'vue';
import { useStore } from 'vuex';
import { useRoute } from 'vue-router';
import { HomeFilled,User,UserFilled, Edit, Lock, Avatar, Notebook, Document, DocumentCopy,PictureRounded,Picture,PictureFilled,Tools,Platform } from '@element-plus/icons-vue'
const store = useStore();
const route = useRoute();
// 使用 computed 获取 Vuex getters
const filteredMenus = computed(() => store.getters.filteredMenus)
const singleMenus = computed(() => store.getters.singleMenus)

const isCollapse = ref(false); // 声明响应式变量
const adminMenus = computed(() => store.state.adminMenus); // 声明计算属性
const currentPath = computed(() => route.path); // 声明计算属性

// console.log('singleMenus 的值:', singleMenus.value);
// console.log('filteredMenus 的值:',  filteredMenus.value);

const iconMap = {
  User,
  UserFilled,
  Edit,
  Lock,
  Avatar,
  Notebook,
  Document,
  DocumentCopy,
  PictureRounded,
  Picture,
  PictureFilled,
  Tools,
  HomeFilled,
  Platform
};


</script>


<style scoped>
.el-menu-admin {
border-radius: 0;
height: 100%;
}
.el-aside {
background-color: #30333c;
}
</style>