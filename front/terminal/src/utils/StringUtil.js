// 将 '/' 转为 '@'
export function changeStr(str) {
  let result = '';
  for (let i = 0; i < str.length; i++) {
    if(str[i] != '/') result += str[i];
    else result += '@';
  }
  
  return result;
}