extern crate alloc;
extern crate core;
extern crate wee_alloc;

use std::mem;
use std::mem::MaybeUninit;
use std::slice;
use std::collections::HashMap;

use serde::{Deserialize, Serialize};
use base64_serde::base64_serde_type;

base64_serde_type!(Base64Standard, base64::engine::general_purpose::STANDARD);

/// Set the global allocator to the WebAssembly optimized one.
//#[global_allocator]
//static ALLOC: wee_alloc::WeeAlloc = wee_alloc::WeeAlloc::INIT;

#[derive(Serialize, Deserialize)]
struct Message {
    headers: HashMap<String, serde_json::Value>,

    #[serde(with = "Base64Standard")]
    body: Vec<u8>,
}

#[cfg_attr(all(target_arch = "wasm32"), export_name = "alloc")]
#[no_mangle]
pub extern "C" fn alloc(size: u32) -> *mut u8 {
   // Allocate the amount of bytes needed.
   let vec: Vec<MaybeUninit<u8>> = Vec::with_capacity(size as usize);

   // into_raw leaks the memory to the caller.
   Box::into_raw(vec.into_boxed_slice()) as *mut u8
}

#[cfg_attr(all(target_arch = "wasm32"), export_name = "dealloc")]
#[no_mangle]
pub unsafe extern "C" fn dealloc(ptr: &mut u8, len: i32) {
    // Retakes the pointer which allows its memory to be freed.
    let _ = Vec::from_raw_parts(ptr, 0, len as usize);
}

#[cfg_attr(all(target_arch = "wasm32"), export_name = "process")]
#[no_mangle]
pub extern fn process(ptr: u32, len: u32) -> u64 {
    let bytes = unsafe { slice::from_raw_parts_mut(ptr as *mut u8, len as usize) };

    let mut msg: Message = serde_json::from_slice(bytes).unwrap();
    msg.body = String::from_utf8(msg.body).unwrap().to_uppercase().as_bytes().to_vec();

    let out_vec = serde_json::to_vec(&msg).unwrap();
    let out_len = out_vec.len() as u32;
    let out_ptr = alloc(out_len);

    unsafe {
        std::ptr::copy_nonoverlapping(out_vec.as_ptr(), out_ptr, out_len as usize);
    }

    return ((out_ptr as u64) << 32) | out_len as u64;
}
