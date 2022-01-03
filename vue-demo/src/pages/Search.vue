<template>
    <el-row>
        <el-col :span="8" :offset="8">
            <el-input v-model="keyword" placeholder="请输入代办事项"></el-input>
        </el-col>
    </el-row>
</template>

<script>
    export default {
        name: "Search",
        data() {
            return {
                keyword: ''
            }
        },
    }
</script>

<style scoped>

</style>