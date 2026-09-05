// ============================================================
// WHATSAPP ORDER CONFIG
// ============================================================
// Replace this with the actual seller/business WhatsApp number,
// in international format WITHOUT a leading + or spaces.

const WHATSAPP_NUMBER = "919022797055"; 

// No backend price field exists on Book (kept out of scope deliberately -
// this is a UI-only order-request flow, not a real e-commerce system).
// A simple, believable price is derived from content type instead.
const PRICE_BY_CONTENT_TYPE = {
  BOOK: 299,
  NOVEL: 349,
  STUDY_BOOK: 499,
  MANGA: 199,
  MANHWA: 199,
  MANHUA: 199,
  COMIC: 249,
  GRAPHIC_NOVEL: 349,
  LIGHT_NOVEL: 279,
  CHILDRENS_BOOK: 199,
};

export function getDemoPrice(contentType) {
  return PRICE_BY_CONTENT_TYPE[contentType] ?? 299;
}

// Builds a wa.me deep link with a pre-filled message. Opening this URL
// launches WhatsApp (app on mobile, WhatsApp Web on desktop) with the
// message box already populated - the actual "order" is then just a
// normal chat conversation the seller replies to manually. No payment
// gateway, no order database, no licensing concern - it's a message.
export function buildWhatsAppOrderLink({ bookTitle, price, quantity, name, address, contact }) {
  const totalAmount = price * quantity;

  const message =
    `Hi! I'd like to order a physical copy from BookVerse.\n\n` +
    `📖 Book: ${bookTitle}\n` +
    `🔢 Quantity: ${quantity}\n` +
    `💰 Price: ₹${price} x ${quantity} = ₹${totalAmount}\n\n` +
    `👤 Name: ${name}\n` +
    `📍 Address: ${address}\n` +
    `📞 Contact: ${contact}\n\n` +
    `Please confirm availability and delivery details. Thank you!`;

  return `https://wa.me/${WHATSAPP_NUMBER}?text=${encodeURIComponent(message)}`;
}
