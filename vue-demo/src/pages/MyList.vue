<template>
    <div class="list-box">
        <my-item v-for="todo in todoList" :key="todo.id" :todo="todo"/>
    </div>
</template>

<script>
    import MyItem from "@/pages/MyItem";
    export default {
        name: "MyList",
        components: {MyItem},

        data() {
            return {
                todoList: []
            }
        },
    }
</script>

<style scoped>
    .list-box {
        background-color: #fef9ff;
        width: 60%;
        margin: 0 auto;
    }
</style>