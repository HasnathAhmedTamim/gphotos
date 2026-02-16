package com.example.photoclone.presentation.viewmodel

import com.example.photoclone.data.model.Photo
import org.junit.Assert.*
import org.junit.Test

class PhotoSelectionViewModelTest {

    @Test
    fun setPhotos_updatesPhotosAndResetsSelection() {
        val vm = PhotoSelectionViewModel()
        val photos = listOf(
            Photo(id = 1, imageUrl = "http://a/1.jpg"),
            Photo(id = 2, imageUrl = "http://a/2.jpg")
        )

        vm.setPhotos(photos)

        assertEquals(2, vm.photos.value.size)
        assertEquals(0, vm.selectedCount.value)
        assertFalse(vm.isSelectionMode.value)
    }

    @Test
    fun startSelectionMode_selectsPhoto_andEnablesSelectionMode() {
        val vm = PhotoSelectionViewModel()
        val photos = listOf(
            Photo(id = 1, imageUrl = "http://a/1.jpg"),
            Photo(id = 2, imageUrl = "http://a/2.jpg")
        )
        vm.setPhotos(photos)

        vm.startSelectionMode(photos[0])

        assertTrue(vm.isSelectionMode.value)
        assertEquals(1, vm.selectedCount.value)
        val selected = vm.selectedPhotos.value
        assertEquals(1, selected.size)
        assertEquals(1L, selected[0].id)
    }

    @Test
    fun toggleSelection_togglesCorrectPhoto() {
        val vm = PhotoSelectionViewModel()
        val photos = listOf(
            Photo(id = 1, imageUrl = "http://a/1.jpg"),
            Photo(id = 2, imageUrl = "http://a/2.jpg")
        )
        vm.setPhotos(photos)

        vm.toggleSelection(photos[1])
        assertTrue(vm.isSelectionMode.value)
        assertEquals(1, vm.selectedCount.value)
        assertTrue(vm.photos.value.find { it.id == 2L }?.isSelected == true)

        vm.toggleSelection(photos[1])
        assertEquals(0, vm.selectedCount.value)
        assertFalse(vm.isSelectionMode.value)
    }

    @Test
    fun clearSelection_clearsAllSelection() {
        val vm = PhotoSelectionViewModel()
        val photos = listOf(
            Photo(id = 1, imageUrl = "http://a/1.jpg", isSelected = true),
            Photo(id = 2, imageUrl = "http://a/2.jpg", isSelected = true)
        )
        vm.setPhotos(photos)

        assertEquals(2, vm.selectedCount.value)
        vm.clearSelection()
        assertEquals(0, vm.selectedCount.value)
        assertFalse(vm.isSelectionMode.value)
        assertTrue(vm.photos.value.all { !it.isSelected })
    }
}
