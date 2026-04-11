const XLSX = require('./node_modules/xlsx');
const fs = require('fs');
const path = require('path');

// 确保 doc 目录存在
const docDir = path.join(__dirname, '..', 'doc');
if (!fs.existsSync(docDir)) {
  fs.mkdirSync(docDir, { recursive: true });
}

// 创建用户导入模板
const workbook = XLSX.utils.book_new();

// 表头
const headers = ['用户名', '昵称', '部门', '手机号', '邮箱', '性别', '状态', '密码'];

// 示例数据
const sampleData = [
  ['testuser01', '测试用户01', '深圳研发部', '13800001001', 'test01@fast.com', '男', '正常', 'test123'],
  ['testuser02', '测试用户02', '深圳研发部', '13800001002', 'test02@fast.com', '女', '正常', ''],
  ['testuser03', '测试用户03', '北京分公司', '13800001003', '', '男', '正常', 'abc123'],
  ['testuser04', '', '市场部', '', '', '未知', '禁用', ''],
];

// 合并表头和数据
const wsData = [headers, ...sampleData];

// 创建工作表
const worksheet = XLSX.utils.aoa_to_sheet(wsData);

// 设置列宽
worksheet['!cols'] = [
  { wch: 15 },  // 用户名
  { wch: 15 },  // 昵称
  { wch: 15 },  // 部门
  { wch: 15 },  // 手机号
  { wch: 20 },  // 邮箱
  { wch: 8 },   // 性别
  { wch: 8 },   // 状态
  { wch: 12 },  // 密码
];

// 添加工作表
XLSX.utils.book_append_sheet(workbook, worksheet, '用户导入');

// 写入文件到 doc 目录
const outputPath = path.join(docDir, '用户导入模板.xlsx');
XLSX.writeFile(workbook, outputPath);

console.log('Excel文件已生成: ' + outputPath);
