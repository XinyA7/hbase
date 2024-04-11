<script>
import { getMovie, SearchMovieByName } from "./api/api";
import { Search } from "@element-plus/icons-vue";
export default {
  data() {
    return {
      startRow: "1",
      pageSize: "8",
      name: "",
      genres: "",
      tableData: [],
      Search,
    };
  },
  created() {
    this.fetchData();
  },
  methods: {
    fetchData() {
      this.tableData = [];
      let data = {
        startRow: this.startRow,
        pageSize: this.pageSize,
      };
      getMovie(JSON.stringify(data)).then((res) => {
        for (let r in res) {
          if (r === "startRow") {
            this.startRow = res.startRow;
          } else {
            let obj = {
              title: res[r].title,
              genres: res[r].genres,
              rating: parseFloat(res[r].rating).toFixed(2),
            };
            this.tableData.push(obj);
          }
        }
      });
    },
    searchByName() {
      this.tableData = [];
      this.startRow = "1";
      let data = {
        name: this.name,
      };
      SearchMovieByName(JSON.stringify(data)).then((res) => {
        console.log(res);
        for (let r in res) {
          let obj = {
            title: res[r].title,
            genres: res[r].genres,
            rating: parseFloat(res[r].rating).toFixed(2),
          };
          this.tableData.push(obj);
        }
      });
    },
    searchByGenres() {
      this.tableData = [];
      this.startRow = "1";
      let data = {
        genres: this.genres,
      };
      SearchMovieByGenres(JSON.genresify(data)).then((res) => {
        console.log(res);
        for (let r in res) {
          let obj = {
            title: res[r].title,
            rating: res[r].rating,
            genres: parseFloat(res[r].genres).toFixed(2),
          };
          this.tableData.push(obj);
        }
      });
    },
  },
};
</script>
<template>
  <div id="head">
    <div id="aaa"><h1>电 影 评 分 查 询</h1></div>
  </div>

  <div id="bbb">
    <div id="b1">
      <div id="b11">电影名：</div>
      
      <div id="b12">
        <el-input
          v-model="name"
          style="max-width: 300px"
          placeholder="请输入电影名"
          class="input-with-select"
        >
          <template #prepend>
            <el-button :icon="Search" @click="searchByName" />
          </template>
        </el-input>
      </div>
    </div>

    <div id="b2">
      <div id="b21">电影类型：</div>
      <div id="b22">
        <el-input
          v-model="genres"
          style="max-width: 300px"
          placeholder="请输入电影类型"
          class="input-with-select"
        >
          <template #prepend>
            <el-button :icon="Search" @click="searchByGenres" />
          </template>
        </el-input>
      </div>
    </div>
  </div>
  <div id="ccc">
    <el-table
      :data="tableData"
      style="
        width: 100%;
        border: 1px solid #ccc;
        border-collapse: collapse;
        font-family: Arial;
      "
    >
      <el-table-column
        prop="title"
        label="电影名称"
        width="450"
        style="background-color: #f2f2f2; color: #333; text-align: center"
      />
      <el-table-column
        prop="genres"
        label="电影类型"
        width="300"
        style="font-weight: bold; color: #007bff"
      />
      <el-table-column
        prop="rating"
        label="评分"
        width="200"
        style="background-color: #ffc107; color: #333; text-align: center"
      />
    </el-table>

    <!-- <el-button type="primary" @click="">上一页</el-button> -->
    <el-button type="primary" @click="fetchData">下一页</el-button>
  </div>
</template>


<style scoped></style>
