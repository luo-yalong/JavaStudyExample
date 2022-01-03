<template>
    <div>
        <my-header :title="title"/>
        <Search/>
        <hr>
        <my-list :todoList="todoList"/>
    </div>
</template>

<script>
    import MyHeader from "@/components/MyHeader";
    import Search from "@/pages/Search";
    import MyList from "@/pages/MyList";
    export default {
        name: "Home",
        components: {MyList, Search, MyHeader},
        data() {
            return {
                title: '测试Vue、Element-UI',
                todoList: [
                    {id:'001',title: '吃饭'},
                    {id:'002',title: '睡觉'},
                    {id:'003',title: '打豆豆'},
                ]
            }
        },
    }
</script>

<style scoped>

</style>