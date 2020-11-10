package com.darkrockstudios.apps.adventurerocksdownloader.photos

import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import coil.load
import com.darkrockstudios.apps.adventurerocksdownloader.R
import com.darkrockstudios.apps.adventurerocksdownloader.database.Photo360
import com.darkrockstudios.apps.adventurerocksdownloader.databinding.PhotoListItemBinding
import com.mikepenz.fastadapter.binding.AbstractBindingItem

data class PhotoListItem(val photo: Photo360): AbstractBindingItem<PhotoListItemBinding>()
{
	override val type: Int = R.id.photo_list_item

	override fun bindView(binding: PhotoListItemBinding, payloads: List<Any>)
	{
		binding.apply {
			photoTitle.text = Html.fromHtml(photo.title, Html.FROM_HTML_MODE_LEGACY)
			photoDate.text = photo.date_taken
			photoDescription.text = Html.fromHtml(photo.description, Html.FROM_HTML_MODE_LEGACY)

			photoPreview.load(photo.preview) {
				crossfade(true)
				placeholder(R.drawable.ic_loading)
			}

			/*
			d(photo.map)
			photoMap.load(photo.map) {
				crossfade(true)
				transformations(BlurTransformation(photoMap.context))
			}*/

			photoDownloaded.isVisible = photo.isDownloaded()
		}
	}

	override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?) = PhotoListItemBinding.inflate(inflater, parent, false)
}